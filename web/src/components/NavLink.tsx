'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

/**
 * Link de navegacion que marca la pagina activa. Se compara con `startsWith`
 * salvo en la raiz del panel, que si no quedaria marcada en todas las rutas.
 */
export default function NavLink({ href, children }: { href: string; children: React.ReactNode }) {
  const pathname = usePathname();
  const activo = href === '/panel' ? pathname === href : pathname.startsWith(href);

  return (
    <Link href={href} aria-current={activo ? 'page' : undefined}>
      {children}
    </Link>
  );
}
