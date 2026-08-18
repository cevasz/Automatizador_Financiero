// Los montos se guardan en CENTAVOS de peso (igual que en Room), asi que todo
// lo que se muestre pasa primero por aqui. Formatear directamente el valor
// crudo mostraria cifras 100 veces mas grandes.

const COP = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0
});

export function formatCents(cents: number): string {
  return COP.format(Math.round(cents) / 100);
}

export function formatCentsSigned(cents: number, type: 'INCOME' | 'EXPENSE'): string {
  return `${type === 'INCOME' ? '+' : '−'} ${formatCents(Math.abs(cents))}`;
}

const DATE_LONG = new Intl.DateTimeFormat('es-CO', {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit'
});

const DATE_SHORT = new Intl.DateTimeFormat('es-CO', {
  day: '2-digit',
  month: 'short'
});

const MONTH_LABEL = new Intl.DateTimeFormat('es-CO', { month: 'short', year: '2-digit' });

export const formatDate = (iso: string) => DATE_LONG.format(new Date(iso));
export const formatDateShort = (iso: string) => DATE_SHORT.format(new Date(iso));
export const formatMonth = (iso: string) => MONTH_LABEL.format(new Date(iso));

export function percent(part: number, whole: number): number {
  if (whole <= 0) return 0;
  return Math.min(100, Math.round((part / whole) * 100));
}

/** Primer instante del mes actual, en ISO — para filtrar "este mes". */
export function startOfCurrentMonth(): string {
  const now = new Date();
  return new Date(now.getFullYear(), now.getMonth(), 1).toISOString();
}
