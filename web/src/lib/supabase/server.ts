import { cookies } from 'next/headers';
import { createServerClient } from '@supabase/ssr';
import { SUPABASE_ANON_KEY, SUPABASE_URL } from './env';

/**
 * Cliente para Server Components, Route Handlers y Server Actions.
 *
 * El `try/catch` alrededor de `set` no es descuido: en un Server Component las
 * cookies son de solo lectura y escribirlas lanza. Ahi el refresco del token lo
 * hace el middleware, que si puede escribir; ignorar el fallo es lo correcto.
 */
export async function createClient() {
  const cookieStore = await cookies();

  return createServerClient(SUPABASE_URL(), SUPABASE_ANON_KEY(), {
    cookies: {
      getAll() {
        return cookieStore.getAll();
      },
      setAll(cookiesToSet) {
        try {
          cookiesToSet.forEach(({ name, value, options }) =>
            cookieStore.set(name, value, options)
          );
        } catch {
          // Server Component: sin permiso de escritura. El middleware refresca.
        }
      }
    }
  });
}
