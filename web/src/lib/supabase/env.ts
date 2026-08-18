// Lee la configuracion de Supabase y falla con un mensaje util si falta, en vez
// de dejar que el error aparezca mas tarde como un 401 opaco desde PostgREST.
function required(name: string, value: string | undefined): string {
  if (!value) {
    throw new Error(
      `Falta ${name}. Copia web/.env.example a web/.env.local y rellena los ` +
        `valores de Supabase → Project Settings → API.`
    );
  }
  return value;
}

export const SUPABASE_URL = () =>
  required('NEXT_PUBLIC_SUPABASE_URL', process.env.NEXT_PUBLIC_SUPABASE_URL);

export const SUPABASE_ANON_KEY = () =>
  required('NEXT_PUBLIC_SUPABASE_ANON_KEY', process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY);
