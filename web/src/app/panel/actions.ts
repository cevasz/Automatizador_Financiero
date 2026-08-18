'use server';

import { revalidatePath } from 'next/cache';
import { createClient } from '@/lib/supabase/server';
import type { ConfirmationState } from '@/lib/types';

/**
 * Acciones de escritura del panel.
 *
 * Regla que aplica a TODAS: cada escritura pone `updated_at` en la hora actual.
 * Sin eso, la resolucion de conflictos del servidor (last-write-wins sobre
 * updated_at, ver backend/supabase/migrations/0002) veria un valor viejo y el
 * proximo push del telefono revertiria en silencio lo que se acaba de corregir
 * aqui.
 *
 * El aislamiento por usuario NO se hace aqui sino con RLS en Postgres: aunque a
 * alguien le llegara un id ajeno, la politica lo rechaza.
 */

type Resultado = { ok: true } | { ok: false; error: string };

async function cliente() {
  const supabase = await createClient();
  const {
    data: { user }
  } = await supabase.auth.getUser();
  if (!user) throw new Error('Sesión expirada. Vuelve a entrar.');
  return { supabase, user };
}

function fallo(e: unknown): Resultado {
  return { ok: false, error: e instanceof Error ? e.message : String(e) };
}

const ahora = () => new Date().toISOString();

// --- Movimientos -----------------------------------------------------------

export async function actualizarMovimiento(
  id: string,
  cambios: { category_id?: string | null; confirmation_state?: ConfirmationState }
): Promise<Resultado> {
  try {
    const { supabase } = await cliente();
    const { error } = await supabase
      .from('movements')
      .update({ ...cambios, updated_at: ahora() })
      .eq('id', id);
    if (error) throw error;

    revalidatePath('/panel/movimientos');
    revalidatePath('/panel');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}

export async function borrarMovimiento(id: string): Promise<Resultado> {
  try {
    const { supabase } = await cliente();
    // Borrado logico: un DELETE fisico volveria a aparecer en el proximo push
    // del telefono, que no tiene forma de saber que se borro aqui.
    const { error } = await supabase
      .from('movements')
      .update({ deleted: true, updated_at: ahora() })
      .eq('id', id);
    if (error) throw error;

    revalidatePath('/panel/movimientos');
    revalidatePath('/panel');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}

// --- Presupuestos ----------------------------------------------------------

export async function guardarPresupuesto(entrada: {
  id?: string;
  category_id: string;
  monthly_limit_pesos: number;
  month: number;
  year: number;
}): Promise<Resultado> {
  try {
    const { supabase, user } = await cliente();

    if (!Number.isFinite(entrada.monthly_limit_pesos) || entrada.monthly_limit_pesos <= 0) {
      throw new Error('El límite debe ser mayor que cero.');
    }

    const fila = {
      ...(entrada.id ? { id: entrada.id } : {}),
      user_id: user.id,
      category_id: entrada.category_id,
      // La UI pide pesos; la base guarda centavos, igual que Room.
      monthly_limit: Math.round(entrada.monthly_limit_pesos * 100),
      month: entrada.month,
      year: entrada.year,
      deleted: false,
      updated_at: ahora()
    };

    const { error } = await supabase.from('budgets').upsert(fila, { onConflict: 'id' });
    if (error) throw error;

    revalidatePath('/panel/presupuestos');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}

export async function borrarPresupuesto(id: string): Promise<Resultado> {
  try {
    const { supabase } = await cliente();
    const { error } = await supabase
      .from('budgets')
      .update({ deleted: true, updated_at: ahora() })
      .eq('id', id);
    if (error) throw error;

    revalidatePath('/panel/presupuestos');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}

// --- Metas de ahorro -------------------------------------------------------

export async function guardarMeta(entrada: {
  id?: string;
  name: string;
  target_pesos: number;
  target_date: string;
}): Promise<Resultado> {
  try {
    const { supabase, user } = await cliente();

    if (!entrada.name.trim()) throw new Error('La meta necesita un nombre.');
    if (!Number.isFinite(entrada.target_pesos) || entrada.target_pesos <= 0) {
      throw new Error('El monto objetivo debe ser mayor que cero.');
    }

    const { error } = await supabase.from('savings_goals').upsert(
      {
        ...(entrada.id ? { id: entrada.id } : {}),
        user_id: user.id,
        name: entrada.name.trim(),
        target_amount: Math.round(entrada.target_pesos * 100),
        target_date: new Date(entrada.target_date).toISOString(),
        deleted: false,
        updated_at: ahora()
      },
      { onConflict: 'id' }
    );
    if (error) throw error;

    revalidatePath('/panel/metas');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}

export async function abonarMeta(id: string, pesos: number): Promise<Resultado> {
  try {
    const { supabase } = await cliente();

    if (!Number.isFinite(pesos) || pesos <= 0) throw new Error('El abono debe ser mayor que cero.');

    // Se lee y se suma en vez de escribir un valor absoluto: el telefono puede
    // haber abonado tambien. (El mismo bug que se corrigio en la app: el DAO
    // hacia SET en vez de sumar y borraba el ahorro previo.)
    const { data, error: errorLectura } = await supabase
      .from('savings_goals')
      .select('current_amount')
      .eq('id', id)
      .single();
    if (errorLectura) throw errorLectura;

    const { error } = await supabase
      .from('savings_goals')
      .update({
        current_amount: (data?.current_amount ?? 0) + Math.round(pesos * 100),
        updated_at: ahora()
      })
      .eq('id', id);
    if (error) throw error;

    revalidatePath('/panel/metas');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}

export async function borrarMeta(id: string): Promise<Resultado> {
  try {
    const { supabase } = await cliente();
    const { error } = await supabase
      .from('savings_goals')
      .update({ deleted: true, updated_at: ahora() })
      .eq('id', id);
    if (error) throw error;

    revalidatePath('/panel/metas');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}

// --- Agenda ----------------------------------------------------------------

export async function guardarContacto(entrada: {
  id?: string;
  account_identifier: string;
  display_name: string;
  default_category_id: string | null;
}): Promise<Resultado> {
  try {
    const { supabase, user } = await cliente();

    if (!entrada.account_identifier.trim()) throw new Error('Falta el número o identificador.');
    if (!entrada.display_name.trim()) throw new Error('Falta el nombre a mostrar.');

    const { error } = await supabase.from('agenda_entries').upsert(
      {
        ...(entrada.id ? { id: entrada.id } : {}),
        user_id: user.id,
        account_identifier: entrada.account_identifier.trim(),
        display_name: entrada.display_name.trim(),
        default_category_id: entrada.default_category_id,
        origin: 'MANUAL',
        deleted: false,
        updated_at: ahora()
      },
      { onConflict: 'id' }
    );
    if (error) throw error;

    revalidatePath('/panel/agenda');
    revalidatePath('/panel/movimientos');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}

export async function borrarContacto(id: string): Promise<Resultado> {
  try {
    const { supabase } = await cliente();
    const { error } = await supabase
      .from('agenda_entries')
      .update({ deleted: true, updated_at: ahora() })
      .eq('id', id);
    if (error) throw error;

    revalidatePath('/panel/agenda');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}

// --- Cuenta (habeas data) --------------------------------------------------

export async function borrarTodosMisDatos(): Promise<Resultado> {
  try {
    const { supabase } = await cliente();
    // RPC y no ocho DELETE: se hace en una transaccion y en el orden correcto
    // de llaves foraneas. Ver 0002_kivo_sync_api.sql.
    const { error } = await supabase.rpc('kivo_delete_all_data');
    if (error) throw error;

    revalidatePath('/panel', 'layout');
    return { ok: true };
  } catch (e) {
    return fallo(e);
  }
}
