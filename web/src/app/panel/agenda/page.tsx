import { createClient } from '@/lib/supabase/server';
import type { AgendaEntry, Category } from '@/lib/types';
import { ErrorDeCarga } from '../page';
import EditorContacto from './EditorContacto';
import BotonBorrar from '@/components/BotonBorrar';
import { borrarContacto } from '../actions';

export const metadata = { title: 'Agenda — Kivo' };

const ORIGEN_ETIQUETA: Record<string, string> = {
  MANUAL: 'Agregado a mano',
  AUTO_DETECTED: 'Detectado automáticamente',
  COMMUNITY_SUGGESTED: 'Sugerido por la comunidad'
};

export default async function AgendaPage() {
  const supabase = await createClient();

  const [contactos, categorias] = await Promise.all([
    supabase.from('agenda_entries').select('*').eq('deleted', false).order('display_name'),
    supabase.from('categories').select('*').eq('deleted', false).order('name')
  ]);

  if (contactos.error) return <ErrorDeCarga mensaje={contactos.error.message} />;

  const filas = (contactos.data ?? []) as AgendaEntry[];
  const cats = (categorias.data ?? []) as Category[];
  const catPorId = new Map(cats.map((c) => [c.id, c]));

  return (
    <div className="stack" style={{ gap: '1.5rem' }}>
      <header className="stack" style={{ gap: '0.25rem' }}>
        <p className="eyebrow">{filas.length} contacto{filas.length === 1 ? '' : 's'}</p>
        <h1>Agenda financiera</h1>
        <p className="muted small" style={{ margin: 0 }}>
          Cuando un número o comercio está aquí, Kivo deja de mostrar el texto crudo del
          banco y usa el nombre y la categoría que definas.
        </p>
      </header>

      <EditorContacto categorias={cats} />

      {filas.length === 0 ? (
        <div className="card vacio">
          <p style={{ margin: 0 }}>La agenda está vacía.</p>
        </div>
      ) : (
        <div className="card" style={{ padding: '1rem' }}>
          <div className="tabla-scroll">
            <table className="tabla">
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Identificador</th>
                  <th>Categoría por defecto</th>
                  <th>Origen</th>
                  <th aria-label="Acciones" />
                </tr>
              </thead>
              <tbody>
                {filas.map((c) => (
                  <tr key={c.id}>
                    <td>{c.display_name}</td>
                    <td className="mono small muted">{c.account_identifier}</td>
                    <td className="small">
                      {c.default_category_id
                        ? (catPorId.get(c.default_category_id)?.name ?? 'Categoría eliminada')
                        : '—'}
                    </td>
                    <td className="tiny muted">{ORIGEN_ETIQUETA[c.origin] ?? c.origin}</td>
                    <td>
                      <div className="row" style={{ gap: '0.4rem' }}>
                        <EditorContacto categorias={cats} existente={c} compacto />
                        <BotonBorrar
                          accion={borrarContacto.bind(null, c.id)}
                          confirmacion={`¿Eliminar a "${c.display_name}" de la agenda?`}
                        />
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
