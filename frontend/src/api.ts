export interface ContractAward {
  awardId: string | null
  recipientName: string | null
  amount: number | null
  awardingAgency: string | null
  startDate: string | null
  endDate: string | null
}

export interface PagedAwards {
  results: ContractAward[]
  page: number
  totalPages: number
  hasNext: boolean
}

export interface AwardSearch {
  startDate: string
  endDate: string
  keywords: string[]
  limit: number
  page: number
}

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''

export async function searchAwards(search: AwardSearch, signal: AbortSignal): Promise<PagedAwards> {
  return parsePagedAwards(await postJson('/api/v1/contracts/search', search, signal))
}

async function postJson(path: string, body: unknown, signal: AbortSignal): Promise<unknown> {
  let response: Response
  try {
    response = await fetch(`${API_BASE}${path}`, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
      signal,
    })
  } catch (error) {
    if (isAbort(error)) throw error
    throw new Error('Could not reach the contracts API. Start the backend on port 8080.')
  }

  if (!response.ok) {
    throw new Error(await readError(response))
  }

  return response.json()
}

export function parseKeywords(value: string): string[] {
  return value
    .split(',')
    .map((keyword) => keyword.trim())
    .filter((keyword) => keyword.length > 0)
}

export function keywordError(value: string): string | null {
  const tooShort = parseKeywords(value).find((keyword) => keyword.length < 3)
  if (!tooShort) return null
  return `Each keyword must be at least 3 characters. "${tooShort}" is too short.`
}

async function readError(response: Response): Promise<string> {
  try {
    const body = (await response.json()) as { message?: unknown; details?: unknown }
    const details = Array.isArray(body.details)
      ? body.details.filter((detail): detail is string => typeof detail === 'string' && detail.length > 0)
      : []
    if (details.length > 0) return details.join(' ')
    if (typeof body.message === 'string' && body.message.length > 0) return body.message
  } catch {
    // The proxy or server returned a non-JSON error body.
  }
  return `Request failed (${response.status}).`
}

function parsePagedAwards(body: unknown): PagedAwards {
  if (!isRecord(body) || !Array.isArray(body.results)) {
    throw new Error('The contracts API returned an unexpected response.')
  }

  return {
    results: body.results.map(parseAward),
    page: wholeNumber(body.page, 1),
    totalPages: wholeNumber(body.totalPages, 0),
    hasNext: body.hasNext === true,
  }
}

function wholeNumber(value: unknown, fallback: number): number {
  return typeof value === 'number' && Number.isInteger(value) && value >= 0 ? value : fallback
}

function parseAward(value: unknown): ContractAward {
  if (!isRecord(value)) {
    return {
      awardId: null,
      recipientName: null,
      amount: null,
      awardingAgency: null,
      startDate: null,
      endDate: null,
    }
  }

  return {
    awardId: text(value.awardId),
    recipientName: text(value.recipientName),
    amount: amount(value.amount),
    awardingAgency: text(value.awardingAgency),
    startDate: text(value.startDate),
    endDate: text(value.endDate),
  }
}

function text(value: unknown): string | null {
  return typeof value === 'string' && value.length > 0 ? value : null
}

function amount(value: unknown): number | null {
  if (typeof value === 'number' && Number.isFinite(value)) return value
  if (typeof value === 'string' && value.trim().length > 0) {
    const parsed = Number(value)
    return Number.isFinite(parsed) ? parsed : null
  }
  return null
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null
}

function isAbort(error: unknown): boolean {
  return error instanceof Error && error.name === 'AbortError'
}
