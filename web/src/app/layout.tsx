import type { Metadata, Viewport } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Kivo — Panel',
  description:
    'Panel web de Kivo: tu historial financiero automático, sincronizado desde la app Android.'
};

export const viewport: Viewport = {
  themeColor: [
    { media: '(prefers-color-scheme: light)', color: '#ded1b8' },
    { media: '(prefers-color-scheme: dark)', color: '#1b140e' }
  ]
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  );
}
