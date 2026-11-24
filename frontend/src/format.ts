const currency = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
})

const dateFormat = new Intl.DateTimeFormat('en-US', {
  month: 'short',
  day: 'numeric',
  year: 'numeric',
})

const integers = new Intl.NumberFormat('en-US')

export function formatCurrency(value: number | null): string {
  if (value == null) return '—'
  return currency.format(value)
}

export function formatDate(value: string | null): string {
  if (!value) return '—'
  const match = /^(\d{4})-(\d{2})-(\d{2})/.exec(value)
  if (!match) return value
  const year = Number(match[1])
  const month = Number(match[2])
  const day = Number(match[3])
  return dateFormat.format(new Date(year, month - 1, day))
}

export function formatNumber(value: number): string {
  return integers.format(value)
}

export function pageTotal(amounts: Array<number | null>): number | null {
  const known = amounts.filter((amount): amount is number => amount != null)
  if (known.length === 0) return null
  return known.reduce((sum, amount) => sum + amount, 0)
}
