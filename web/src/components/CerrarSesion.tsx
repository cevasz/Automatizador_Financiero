'use client';

import { useRouter } from 'next/navigation';
import { createClient } from '@/lib/supabase/client';

export default function CerrarSesion() {
  const router = useRouter();

  return (
    <button
      className="btn btn--ghost btn--sm"
      onClick={async () => {
        await createClient().auth.signOut();
        router.refresh();
        router.push('/entrar');
      }}
    >
      Cerrar sesión
    </button>
  );
}
