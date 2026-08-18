// Espejo en TypeScript de las tablas de backend/supabase/migrations/.
// Los montos son bigint en Postgres pero llegan como number por JSON: en
// centavos de peso, un saldo tendria que pasar de ~90 billones de pesos para
// perder precision en un double, asi que no hace falta BigInt aqui.

export type MovementType = 'INCOME' | 'EXPENSE';
export type ConfirmationState = 'PENDING' | 'CONFIRMED' | 'REJECTED' | 'AUTO_CONFIRMED';
export type MovementSource = 'NOTIFICATION' | 'OCR' | 'MANUAL' | 'OPEN_FINANCE' | 'IMPORT';

export interface Category {
  id: string;
  name: string;
  type: MovementType;
  icon_name: string;
  is_custom: boolean;
  parent_category_id: string | null;
  sort_order: number;
  created_at: string;
  updated_at: string;
  deleted: boolean;
}

export interface AgendaEntry {
  id: string;
  account_identifier: string;
  display_name: string;
  default_category_id: string | null;
  color: number;
  origin: string;
  created_at: string;
  updated_at: string;
  deleted: boolean;
}

export interface Movement {
  id: string;
  type: MovementType;
  amount: number;
  payment_method: string;
  counterparty_raw: string;
  counterparty_id: string | null;
  category_id: string | null;
  date: string;
  source: MovementSource;
  confirmation_state: ConfirmationState;
  bank_entity: string;
  raw_text: string;
  created_at: string;
  updated_at: string;
  deleted: boolean;
}

export interface Budget {
  id: string;
  category_id: string;
  monthly_limit: number;
  month: number;
  year: number;
  created_at: string;
  updated_at: string;
  deleted: boolean;
}

export interface SavingsGoal {
  id: string;
  name: string;
  target_amount: number;
  current_amount: number;
  target_date: string;
  created_at: string;
  updated_at: string;
  deleted: boolean;
}

export interface MonthlySummaryRow {
  mes: string;
  type: MovementType;
  movimientos: number;
  total: number;
}

export const BANK_LABELS: Record<string, string> = {
  NEQUI: 'Nequi',
  BANCOLOMBIA: 'Bancolombia',
  DAVIPLATA: 'Daviplata',
  NU: 'Nu',
  LULO: 'Lulo Bank',
  UNKNOWN: 'Sin identificar'
};

export const STATE_LABELS: Record<ConfirmationState, string> = {
  PENDING: 'Por confirmar',
  CONFIRMED: 'Confirmado',
  AUTO_CONFIRMED: 'Confirmado automáticamente',
  REJECTED: 'Rechazado'
};

export const SOURCE_LABELS: Record<MovementSource, string> = {
  NOTIFICATION: 'Notificación bancaria',
  OCR: 'Escaneo (OCR)',
  MANUAL: 'Registro manual',
  IMPORT: 'Extracto importado',
  OPEN_FINANCE: 'Open Finance'
};
