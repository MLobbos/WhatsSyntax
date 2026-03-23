import { authMiddleware } from '@clerk/nextjs';

export default authMiddleware({
  // Routes accessible without authentication
  publicRoutes: ['/', '/book/:slug*', '/sign-in', '/sign-up'],
});

export const config = {
  matcher: ['/((?!.+\\.[\\w]+$|_next).*)', '/', '/(api|trpc)(.*)'],
};
