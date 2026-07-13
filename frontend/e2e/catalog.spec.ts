import { expect, test } from '@playwright/test';
import { CATALOG_BOOK } from './fixtures/seed-data';

test.describe('Consultation du catalogue', () => {
  test('affiche la liste des livres avec leur disponibilité', async ({ page }) => {
    await page.goto('/books');

    const bookCard = page.locator('li', { hasText: CATALOG_BOOK.title });
    await expect(bookCard).toBeVisible();
    await expect(bookCard).toContainText(CATALOG_BOOK.author);
    await expect(bookCard).toContainText(`${CATALOG_BOOK.totalCopies}/${CATALOG_BOOK.totalCopies} exemplaires`);
  });
});
