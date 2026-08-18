import { redirect } from 'next/navigation';

// La raiz no tiene contenido propio: el middleware ya decide si hay sesion, asi
// que mandar a /panel deja que el middleware redirija a /entrar si hace falta.
export default function Home() {
  redirect('/panel');
}
