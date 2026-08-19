# Política de seguridad

## Reportar una vulnerabilidad

Kivo procesa información financiera personal. Si encuentras un problema de
seguridad, **no abras un issue público**.

Escribe a **sebasrincon0427@gmail.com** con:

- Una descripción del problema y de su impacto.
- Los pasos para reproducirlo.
- La versión afectada (aparece en la pantalla de Cuenta).

Recibirás acuse en un plazo razonable. Como el proyecto lo mantiene una sola
persona, no hay compromiso formal de tiempos de respuesta ni programa de
recompensas.

## Alcance

Entra en alcance el código de este repositorio: la app Android, el panel web y
el esquema SQL de `backend/`.

Es de especial interés cualquier fallo que permita:

- Que un usuario alcance datos de otro (fuga de Row Level Security).
- Extraer la base de datos local de un dispositivo sin desbloquearlo.
- Que el panel web escriba fuera de `web/src/app/panel/actions.ts`.

## Fuera de alcance

- Que la `anon key` de Supabase sea visible en el APK o en el bundle JS: **es
  pública por diseño**. Lo que protege los datos es Row Level Security. Sin un
  JWT de sesión válido esa llave no devuelve ninguna fila.
- Un analizador bancario que interprete mal una notificación. Es un error de
  precisión, no de seguridad: repórtalo como issue normal con el texto exacto.
- Ataques que requieran acceso físico a un dispositivo ya desbloqueado.
