import { expect, test } from '@playwright/test';
import {
  ACTIVE_MEMBER_ID,
  BORROWABLE_BOOK_ID,
  MEMBER_AT_LOAN_LIMIT_ID,
  SUSPENDED_MEMBER_ID
} from './fixtures/seed-data';

test.describe("Emprunt d'un livre", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/loans');
  });

  test('un emprunt réussi affiche une confirmation', async ({ page }) => {
    await page.getByLabel('Membre (ID)').fill(ACTIVE_MEMBER_ID);
    await page.getByLabel('Livre').selectOption(BORROWABLE_BOOK_ID);

    const [borrowResponse] = await Promise.all([
      page.waitForResponse((res) => res.url().includes('/api/loans') && res.request().method() === 'POST'),
      page.getByRole('button', { name: 'Emprunter' }).click()
    ]);

    await expect(page.getByText('Emprunt enregistré avec succès.')).toBeVisible();

    // Rend le prêt aussitôt la confirmation vérifiée : sans ce nettoyage, ACTIVE_MEMBER_ID
    // atteindrait la limite de 3 emprunts actifs au bout de 3 exécutions locales successives
    // sans redémarrage du backend, et ce test échouerait alors pour une mauvaise raison.
    const loan = (await borrowResponse.json()) as { id: string };
    await page.request.post(`/api/loans/${loan.id}/return`);
  });

  test("un membre suspendu se voit refuser l'emprunt avec le message correspondant", async ({ page }) => {
    await page.getByLabel('Membre (ID)').fill(SUSPENDED_MEMBER_ID);
    await page.getByLabel('Livre').selectOption(BORROWABLE_BOOK_ID);
    await page.getByRole('button', { name: 'Emprunter' }).click();

    await expect(page.getByRole('alert')).toContainText('cannot borrow a book');
  });

  test("un membre ayant déjà 3 emprunts actifs se voit refuser l'emprunt avec le message correspondant", async ({
    page
  }) => {
    await page.getByLabel('Membre (ID)').fill(MEMBER_AT_LOAN_LIMIT_ID);
    await page.getByLabel('Livre').selectOption(BORROWABLE_BOOK_ID);
    await page.getByRole('button', { name: 'Emprunter' }).click();

    await expect(page.getByRole('alert')).toContainText('already has 3 active loans');
  });
});
