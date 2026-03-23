// Public booking page — no auth required
// Phase 4: Implement service selection, date picker, booking form

interface BookingPageProps {
  params: { slug: string };
}

export default function BookingPage({ params }: BookingPageProps) {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-8">
      <div className="bg-white rounded-xl shadow-sm p-8 w-full max-w-md">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">Book an Appointment</h1>
        <p className="text-gray-500 mb-6">Salon: {params.slug}</p>
        {/* Phase 4: Service selector, date/time picker, contact form */}
        <p className="text-sm text-gray-400 text-center">
          Booking widget coming in Phase 4
        </p>
      </div>
    </div>
  );
}
