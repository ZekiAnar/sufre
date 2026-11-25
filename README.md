# Süfrə — ailə arıqlama menyusu

Şəxsi və ailəvi arıqlama menyusu: 8 mətbəx (Azərbaycan, Türk, Gürcü, İtalyan, Yunan, Çin, Hind, Meksika), 100-dən çox resept, tarixli 28 günlük menyu, hər gün üçün mətbəx seçimi, ailə porsiyaları, alış-veriş siyahısı, su və çəki izləmə, şəkilli məşq planı (həftədə 3 gün, 13 həftəlik mərhələlər, avtomatik çəki tövsiyəsi və məşqdə yandırılan kalorinin günün hədəfinə əlavəsi).

## Quraşdırma

**Android:** [Sufre.apk faylını yükləyin](../../releases/latest/download/Sufre.apk), telefonda açın və "Quraşdır" düyməsinə basın. Telefon icazə soruşsa, "Bu mənbədən quraşdırmağa icazə ver" seçin.

**iPhone:** Safari-də veb versiyanı açın ([zekianar.github.io/sufre](https://zekianar.github.io/sufre/)), "Paylaş" → "Ana ekrana əlavə et". App tam ekranda, ikonla açılır və internetsiz də işləyir.

## Necə yığılır

`main` budağına hər dəyişiklik göndəriləndə GitHub Actions avtomatik olaraq:
1. Capacitor ilə Android layihəsi yaradır və `Sufre.apk` faylını yığır (Releases bölməsində görünür);
2. `www` qovluğunu `gh-pages` budağına göndərir (iPhone və brauzer versiyası).

GitHub Pages ilk dəfə avtomatik açılmasa: **Settings → Pages → Source: Deploy from a branch → `gh-pages` / root → Save**.

## Qeydlər

- Məlumatlar telefonun özündə saxlanılır. Telefonu dəyişəndə "Ailə → Ehtiyat nüsxə" ilə faylı yükləyib yeni telefonda bərpa edin.
- App Store-da yerləşdirmək üçün Mac kompüter, Xcode və Apple Developer hesabı ($99/il) lazımdır: `npm install && npx cap add ios && npx cap open ios`.
- Bu app ümumi məlumat verir və həkim məsləhətini əvəz etmir.
