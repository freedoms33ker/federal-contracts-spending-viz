import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { keywordError, parseKeywords, searchAwards } from './api'
import type { AwardSearch, PagedAwards } from './api'
import { formatCurrency, formatDate, formatNumber, pageTotal } from './format'
import './App.css'

const PAGE_SIZES = [10, 25, 50, 100] as const

interface SearchForm {
  startDate: string
  endDate: string
  keywords: string
  limit: number
}

const INITIAL_FORM: SearchForm = {
  startDate: '2024-10-01',
  endDate: '2025-09-30',
  keywords: '',
  limit: 25,
}

function App() {
  const [form, setForm] = useState<SearchForm>(INITIAL_FORM)
  const [submitted, setSubmitted] = useState<SearchForm>(INITIAL_FORM)
  const [page, setPage] = useState(1)
  const [pageDraft, setPageDraft] = useState('1')
  const [data, setData] = useState<PagedAwards | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const controller = new AbortController()
    const search: AwardSearch = {
      startDate: submitted.startDate,
      endDate: submitted.endDate,
      keywords: parseKeywords(submitted.keywords),
      limit: submitted.limit,
      page,
    }

    searchAwards(search, controller.signal)
      .then((response) => {
        if (controller.signal.aborted) return
        setData(response)
        setPageDraft(String(response.page))
        setLoading(false)
      })
      .catch((caught: unknown) => {
        if (controller.signal.aborted || (caught instanceof Error && caught.name === 'AbortError')) return
        setData(null)
        setError(caught instanceof Error ? caught.message : 'Unable to load award amounts.')
        setLoading(false)
      })

    return () => controller.abort()
  }, [submitted, page])

  function beginRequest() {
    setData(null)
    setError(null)
    setLoading(true)
  }

  function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (form.startDate > form.endDate) {
      setError('Start date must be on or before end date.')
      return
    }
    const invalidKeyword = keywordError(form.keywords)
    if (invalidKeyword) {
      setError(invalidKeyword)
      return
    }
    beginRequest()
    setPage(1)
    setPageDraft('1')
    setSubmitted({ ...form })
  }

  function jumpToPage(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!data || data.totalPages < 1) return
    const next = Number(pageDraft)
    if (!Number.isInteger(next) || next < 1 || next > data.totalPages) {
      setError(`Enter a page from 1 to ${formatNumber(data.totalPages)}.`)
      return
    }
    if (next === page) return
    showPage(next)
  }

  function showPage(next: number) {
    beginRequest()
    setPage(next)
  }

  const total = data ? pageTotal(data.results.map((award) => award.amount)) : null

  return (
    <>
      <header className="masthead">
        <div className="wrap">
          <p className="eyebrow">Federal contracts</p>
          <h1>Award amounts</h1>
          <p className="lede">
            Contract awards from the spending service, ranked by amount. Figures come from USAspending
            through the local API.
          </p>
        </div>
      </header>

      <main className="wrap">
        <form className="filters" onSubmit={onSubmit}>
          <label>
            Start date
            <input
              type="date"
              name="startDate"
              value={form.startDate}
              required
              onChange={(event) => setForm({ ...form, startDate: event.target.value })}
            />
          </label>
          <label>
            End date
            <input
              type="date"
              name="endDate"
              value={form.endDate}
              required
              onChange={(event) => setForm({ ...form, endDate: event.target.value })}
            />
          </label>
          <label>
            Keywords
            <input
              type="text"
              name="keywords"
              value={form.keywords}
              placeholder="satellite, radar"
              autoComplete="off"
              onChange={(event) => setForm({ ...form, keywords: event.target.value })}
            />
          </label>
          <label>
            Per page
            <select
              name="limit"
              value={form.limit}
              onChange={(event) => setForm({ ...form, limit: Number(event.target.value) })}
            >
              {PAGE_SIZES.map((size) => (
                <option key={size} value={size}>
                  {size}
                </option>
              ))}
            </select>
          </label>
          <button type="submit" disabled={loading}>
            {loading ? 'Searching…' : 'Search'}
          </button>
        </form>

        <section className="results" aria-busy={loading} aria-live="polite">
          {loading && (
            <p className="status" role="status">
              Loading award amounts…
            </p>
          )}
          {error && (
            <p className="error" role="alert">
              {error}
            </p>
          )}
          {data && data.results.length === 0 && !loading && !error && (
            <p className="status">No awards matched this search.</p>
          )}
          {data && data.results.length > 0 && (
            <>
              <div className="summary">
                <p>
                  Page {data.page} of {formatNumber(data.totalPages)}
                  <span aria-hidden="true"> · </span>
                  <span>
                    {data.results.length} {data.results.length === 1 ? 'award' : 'awards'}
                  </span>
                </p>
                <p className="page-total">
                  <span>Page total</span>
                  <strong>{formatCurrency(total)}</strong>
                </p>
              </div>
              <div className="table-wrap">
                <table>
                  <caption className="sr-only">
                    Contract award amounts for {formatDate(submitted.startDate)} to{' '}
                    {formatDate(submitted.endDate)}
                  </caption>
                  <thead>
                    <tr>
                      <th scope="col">Award ID</th>
                      <th scope="col">Recipient</th>
                      <th scope="col" className="num">
                        Award amount
                      </th>
                      <th scope="col">Awarding agency</th>
                      <th scope="col">Start</th>
                      <th scope="col">End</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.results.map((award, index) => (
                      <tr key={`${award.awardId ?? 'award'}-${index}`}>
                        <td>{award.awardId ?? '—'}</td>
                        <td>{award.recipientName ?? '—'}</td>
                        <td className="num amount">{formatCurrency(award.amount)}</td>
                        <td>{award.awardingAgency ?? '—'}</td>
                        <td>{formatDate(award.startDate)}</td>
                        <td>{formatDate(award.endDate)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <form className="pager" onSubmit={jumpToPage}>
                <button type="button" disabled={loading || page <= 1} onClick={() => showPage(page - 1)}>
                  Previous
                </button>
                <label>
                  Page
                  <input
                    type="number"
                    name="page"
                    min={1}
                    inputMode="numeric"
                    value={pageDraft}
                    required
                    onChange={(event) => setPageDraft(event.target.value)}
                  />
                </label>
                <span className="page-count">of {formatNumber(data.totalPages)}</span>
                <button type="submit" disabled={loading}>
                  Go
                </button>
                <button type="button" disabled={loading || page >= data.totalPages} onClick={() => showPage(page + 1)}>
                  Next
                </button>
              </form>
            </>
          )}
        </section>
      </main>
    </>
  )
}

export default App
