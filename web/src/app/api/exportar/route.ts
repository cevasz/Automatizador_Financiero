import { NextResponse } from 'next/server';
import { createClient } from '@/lib/supabase/server';

/**
 * Exportacion de datos del usuario (Ley 1581 de 2012, derecho de portabilidad).
 *
 * Es un Route Handler y no una descarga generada en el navegador porque el
 * historico completo puede ser de decenas de miles de filas: armarlo en el
 * cliente obliga a bajarlo todo a memoria primero.
 */
export async function GET(request: Request) {
  const supabase = await createClient();
  const {
    data: { user }
  } = await supabase.auth.getUser();

  if (!user) return NextResponse.json({ error: 'Sesión requerida' }, { status: 401 });

  const formato = new URL(request.url).searchParams.get('formato') === 'csv' ? 'csv' : 'json';
  const fecha = new Date().toISOString().slice(0, 10);

  if (formato === 'csv') {
    // CSV solo de movimientos: es la tabla que la gente lleva a Excel. El resto
    // (agenda, presupuestos, metas) no encaja en una sola tabla plana y va en JSON.
    const [{ data: movimientos }, { data: categorias }] = await Promise.all([
      supabase.from('movements').select('*').eq('deleted', false).order('date', { ascending: false }),
      supabase.from('categories').select('id, name')
    ]);

    const nombreCategoria = new Map((categorias ?? []).map((c) => [c.id as string, c.name as string]));

    const encabezado = 'fecha,tipo,monto_pesos,contraparte,categoria,banco,estado,origen';
    const lineas = (movimientos ?? []).map((m) =>
      [
        new Date(m.date as string).toISOString(),
        m.type,
        // Centavos → pesos con dos decimales: quien abra esto en Excel espera
        // pesos, no el entero interno.
        ((m.amount as number) / 100).toFixed(2),
        csv(m.counterparty_raw as string),
        csv(nombreCategoria.get(m.category_id as string) ?? ''),
        m.bank_entity,
        m.confirmation_state,
        m.source
      ].join(',')
    );

    // BOM UTF-8: sin el, Excel en Windows muestra "Ã±" donde va una ñ.
    const cuerpo = '﻿' + [encabezado, ...lineas].join('\r\n');

    return new NextResponse(cuerpo, {
      headers: {
        'Content-Type': 'text/csv; charset=utf-8',
        'Content-Disposition': `attachment; filename="kivo-movimientos-${fecha}.csv"`
      }
    });
  }

  const tablas = [
    'categories',
    'agenda_entries',
    'movements',
    'budgets',
    'savings_goals',
    'classification_rules',
    'invoices',
    'invoice_items'
  ] as const;

  const resultados = await Promise.all(tablas.map((t) => supabase.from(t).select('*')));

  const snapshot: Record<string, unknown> = {
    exportado_en: new Date().toISOString(),
    usuario: user.email
  };
  tablas.forEach((t, i) => {
    snapshot[t] = resultados[i].data ?? [];
  });

  return new NextResponse(JSON.stringify(snapshot, null, 2), {
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
      'Content-Disposition': `attachment; filename="kivo-datos-${fecha}.json"`
    }
  });
}

/** Escapa un campo de CSV. Un nombre con coma parte la fila en dos sin esto. */
function csv(valor: string): string {
  const v = valor ?? '';
  return /[",\r\n]/.test(v) ? `"${v.replace(/"/g, '""')}"` : v;
}
