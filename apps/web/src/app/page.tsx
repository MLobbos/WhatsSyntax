export default function HomePage() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <div className="text-center">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">SalonFlow</h1>
        <p className="text-xl text-gray-600 mb-8">
          WhatsApp-first booking automation for hair salons &amp; beauty studios
        </p>
        <a
          href="/dashboard"
          className="inline-block bg-brand-600 text-white px-6 py-3 rounded-lg font-medium hover:bg-brand-500 transition-colors"
        >
          Go to Dashboard
        </a>
      </div>
    </main>
  );
}
