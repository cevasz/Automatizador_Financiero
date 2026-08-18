import { redirect } from 'next/navigation';
import { createClient } from '@/lib/supabase/server';
import NavLink from '@/components/NavLink';
import CerrarSesion from '@/components/CerrarSesion';

// Nada de este panel tiene sentido cacheado: son datos de una sola persona que
// cambian cada vez que el telefono sincroniza.
export const dynamic = 'force-dynamic';

export default async function PanelLayout({ children }: { children: React.ReactNode }) {
  const supabase = await createClient();
  const {
    data: { user }
  } = await supabase.auth.getUser();

  // El middleware ya protege /panel; esto es la segunda cerradura, por si la
  // ruta llega por un camino que el matcher no cubra.
  if (!user) redirect('/entrar');

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="marca">
          <strong>Kivo</strong>
          <span className="tiny muted">panel</span>
        </div>

        <nav className="nav">
          <NavLink href="/panel">Inicio</NavLink>
          <NavLink href="/panel/movimientos">Movimientos</NavLink>
          <NavLink href="/panel/presupuestos">Presupuestos</NavLink>
          <NavLink href="/panel/metas">Metas</NavLink>
          <NavLink href="/panel/agenda">Agenda</NavLink>
          <NavLink href="/panel/cuenta">Cuenta</NavLink>
        </nav>

        <div className="pie stack" style={{ marginTop: 'auto', gap: '0.5rem' }}>
          <span className="tiny muted" style={{ wordBreak: 'break-all' }}>
            {user.email}
          </span>
          <CerrarSesion />
        </div>
      </aside>

      <main className="contenido">{children}</main>
    </div>
  );
}
