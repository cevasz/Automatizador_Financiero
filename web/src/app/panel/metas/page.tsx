import { createClient } from '@/lib/supabase/server';
import { formatCents, percent } from '@/lib/format';
import type { SavingsGoal } from '@/lib/types';
import { ErrorDeCarga } from '../page';
import EditorMeta from './EditorMeta';
import AbonarMeta from './AbonarMeta';
import BotonBorrar from '@/components/BotonBorrar';
import { borrarMeta } from '../actions';

export const metadata = { title: 'Metas — Kivo' };

export default async function MetasPage() {
  const supabase = await createClient();
  const { data, error } = await supabase
    .from('savings_goals')
    .select('*')
    .eq('deleted', false)
    .order('target_date');

  if (error) return <ErrorDeCarga mensaje={error.message} />;

  const metas = (data ?? []) as SavingsGoal[];

  return (
    <div className="stack" style={{ gap: '1.5rem' }}>
      <header className="stack" style={{ gap: '0.25rem' }}>
        <p className="eyebrow">{metas.length} meta{metas.length === 1 ? '' : 's'}</p>
        <h1>Metas de ahorro</h1>
      </header>

      <EditorMeta />

      {metas.length === 0 ? (
        <div className="card vacio">
          <p style={{ margin: 0 }}>Todavía no tienes metas de ahorro.</p>
        </div>
      ) : (
        <div className="grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))' }}>
          {metas.map((m) => {
            const pct = percent(m.current_amount, m.target_amount);
            const lograda = m.current_amount >= m.target_amount;
            const dias = Math.ceil((new Date(m.target_date).getTime() - Date.now()) / 86_400_000);

            return (
              <article key={m.id} className="card stack" style={{ gap: '0.6rem' }}>
                <div className="row row--between">
                  <h3>{m.name}</h3>
                  {lograda ? <span className="tag tag--ingreso">Lograda</span> : <span className="tag">{pct}%</span>}
                </div>

                <div className="progreso">
                  <span style={{ width: `${pct}%` }} />
                </div>

                <div className="row row--between small">
                  <span className="numero">{formatCents(m.current_amount)}</span>
                  <span className="muted numero">de {formatCents(m.target_amount)}</span>
                </div>

                <span className="tiny muted">
                  {lograda
                    ? '¡Ya la completaste!'
                    : dias >= 0
                      ? `Faltan ${dias} día${dias === 1 ? '' : 's'}`
                      : `La fecha objetivo pasó hace ${Math.abs(dias)} día${Math.abs(dias) === 1 ? '' : 's'}`}
                </span>

                <div className="row wrap" style={{ gap: '0.5rem' }}>
                  <AbonarMeta id={m.id} />
                  <EditorMeta existente={m} compacto />
                  <BotonBorrar accion={borrarMeta.bind(null, m.id)} confirmacion={`¿Eliminar la meta "${m.name}"?`} />
                </div>
              </article>
            );
          })}
        </div>
      )}
    </div>
  );
}
