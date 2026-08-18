'use client';

import { createBrowserClient } from '@supabase/ssr';
import { SUPABASE_ANON_KEY, SUPABASE_URL } from './env';

/**
 * Cliente para componentes que corren en el navegador (formularios, edicion en
 * linea). La sesion vive en cookies, compartida con el servidor, para que el
 * render del servidor ya sepa quien es el usuario y no haya un parpadeo de
 * "cargando" en cada pantalla.
 */
export function createClient() {
  return createBrowserClient(SUPABASE_URL(), SUPABASE_ANON_KEY());
}
