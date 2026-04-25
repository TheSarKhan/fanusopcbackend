-- Admin user (password: Fanus@2024, bcrypt hashed)
INSERT INTO users (email, password, role) VALUES
('admin@fanus.az', '$2a$12$K9TpQCzH4OW8sVZ7Rz.5ueS5FAXM3B1C8A6QJx6mHrZZ2uD2YU9.', 'ADMIN');

-- Psychologists
INSERT INTO psychologists (name, title, experience, sessions_count, rating, photo_url, accent_color, bg_color, display_order) VALUES
('Aynur Məmmədova',  'Klinik Psixoloq',    '8 il',  '400+', '4.9', 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=300&h=200&fit=crop&crop=face', '#3B6FA5', '#EEF5FF', 1),
('Elnur Hüseynov',   'Psixoterapevt',       '11 il', '600+', '4.8', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=200&fit=crop&crop=face', '#5B4DA8', '#F0EEFF', 2),
('Lalə Əliyeva',     'Ailə Psixoloqu',      '6 il',  '280+', '5.0', 'https://images.unsplash.com/photo-1551836022-deb4988cc6c0?w=300&h=200&fit=crop&crop=face', '#1E7A6E', '#E8F7F5', 3),
('Rəşad Quliyev',    'İDT Mütəxəssisi',     '9 il',  '450+', '4.9', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300&h=200&fit=crop&crop=face', '#3B6FA5', '#EEF5FF', 4),
('Sevinc Babayeva',  'Pozitiv Psixoloq',    '5 il',  '200+', '4.7', 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=300&h=200&fit=crop&crop=face', '#A0522D', '#FFF3EC', 5),
('Tural İsmayılov',  'Nevro-Psixoloq',      '12 il', '700+', '5.0', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300&h=200&fit=crop&crop=face', '#1A5C8A', '#E6F2FA', 6);

-- Specializations
INSERT INTO psychologist_specializations (psychologist_id, specialization) VALUES
(1, 'Depressiya'), (1, 'Narahatlıq'), (1, 'Münasibətlər'),
(2, 'Stress'), (2, 'Travma'), (2, 'Özünüinam'),
(3, 'Ailə münaqişəsi'), (3, 'Uşaq psixologiyası'), (3, 'Valideyinlik'),
(4, 'OKB'), (4, 'Fobiyalar'), (4, 'Yemək pozuntuları'),
(5, 'Şəxsi inkişaf'), (5, 'Motivasiya'), (5, 'Karyera'),
(6, 'ADHD'), (6, 'Yuxu problemləri'), (6, 'Hiperaktivlik');

-- Stats
INSERT INTO stats (stat_value, suffix, label, sub_label, display_order) VALUES
(500,  '+', 'Aktiv müştəri',        'Platforma üzərindən', 1),
(1200, '+', 'Tamamlanmış seans',    'Uğurla başa çatıb',   2),
(98,   '%', 'Müştəri məmnuniyyəti', 'Ortalama reytinq',    3),
(15,   '+', 'Sertifikatlı psixoloq','Müxtəlif ixtisaslar', 4);

-- Announcements
INSERT INTO announcements (category, category_color, category_bg, title, excerpt, published_date, icon_type) VALUES
('Yenilik',   '#3B6FA5', '#E4EEF8', 'Yeni: Qrup terapiya seansları başlayır',
 'Oxşar problemləri olan insanlarla birlikdə hərəkət etmək bəzən daha güclü nəticə verir. Mart 2025-dən etibarən həftəlik qrup seanslarına qoşula bilərsiniz.',
 '2025-03-15', 'GROUP'),
('Kampaniya', '#7B85C8', '#EDE9F8', 'İlk seans üçün 20% endirim',
 'Aprel ayı ərzində ilk dəfə müraciət edən müştərilər üçün xüsusi 20% endirim kampaniyası fəaliyyət göstərir. Bu fürsəti qaçırmayın.',
 '2025-04-01', 'STAR'),
('Tədbirlər', '#3B6FA5', '#E4EEF8', 'Pulsuz vebinar: Stress idarəetməsi',
 'Psixoloq Aynur Məmmədova iş yeri stresi mövzusunda pulsuz onlayn seminar keçirəcəkdir. Qeydiyyat linki tezliklə paylaşılacaq.',
 '2025-04-20', 'VIDEO');

-- Blog posts
INSERT INTO blog_posts (category, category_color, category_bg, title, excerpt, read_time_minutes, published_date, emoji, slug, featured) VALUES
('Narahatlıq',   '#3B6FA5', '#E4EEF8', 'Günlük narahatlığı necə idarə etmək olar?',
 'Narahatlıq hissi hər kəsin həyatının bir parçasıdır. Lakin bu hiss həddini aşanda gündəlik həyata mane ola bilər. Bəzi sadə üsullarla...',
 5, '2025-03-10', '🌿', 'gunluk-narahatliqi-nece-idare-etmek-olar', TRUE),
('Münasibətlər', '#7B85C8', '#EDE9F8', 'Sağlam münasibət qurmağın 7 açarı',
 'Sağlam münasibətlər yaranmır — qurulur. Emosional yetkinlik, kommunikasiya bacarığı və özünü tanımaq bu prosesdə kritik rol oynayır...',
 7, '2025-03-05', '💚', 'saglam-munasibat-qurmaqin-7-acari', FALSE),
('Özünü Tanı',   '#3B6FA5', '#E4EEF8', 'Mindfulness: anda qalmağın sənəti',
 'Mindfulness təcrübəsi stressin azaldılmasında elmi cəhətdən sübuta yetirilmiş bir üsuldur. Hər gün 10 dəqiqəlik praktika ilə...',
 4, '2025-02-28', '🧘', 'mindfulness-anda-qalmaghin-seneti', FALSE),
('Depressiya',   '#7B85C8', '#EDE9F8', 'Depressiya ilə yaşamaq mümkündür',
 'Depressiya zəiflik deyil, tibbi bir vəziyyətdir. Düzgün dəstək, terapiya və gündəlik alışqanlıqlarla depressiyadan çıxmaq mümkündür...',
 8, '2025-02-20', '🌅', 'depressiya-ile-yasamaq-mumkundur', FALSE);

-- FAQs
INSERT INTO faqs (question, answer, display_order) VALUES
('Seanslar necə keçirilir?',
 'Seanslar həm onlayn (video görüntülü zəng vasitəsilə), həm də üz-üzə formatlarda keçirilir. Hər seans orta 50-60 dəqiqə çəkir. Psixoloq ilə birlikdə sizin vəziyyətinizə uyğun seans tezliyi müəyyənləşdirilir.',
 1),
('Gizlilik qorunur mu?',
 'Bəli, tam olaraq. Bütün məlumatlarınız GDPR standartlarına uyğun şəkildə qorunur. Psixoloqlarımız etik qaydalara bağlıdır və heç bir məlumatınız üçüncü şəxslərlə paylaşılmır. Yalnız qanunun tələb etdiyi istisnalar mövcuddur.',
 2),
('Qiymətlər necədir?',
 'Qiymətlər psixoloqa, seans növünə (fərdi, ailə, qrup) və formata (onlayn/üz-üzə) görə fərqlənir. Hər psixoloğun profilində ətraflı qiymət məlumatı mövcuddur. İlk seans üçün xüsusi endirimlər mümkündür.',
 3),
('İlk görüş necə olur?',
 'İlk görüşdə psixoloq sizinlə tanış olur, hazırki vəziyyətinizi, narahatlıqlarınızı və hədəflərinizi anlayır. Bu, qarşılıqlı tanışlıq seansdır — sizi mühakimə etmədən, sadəcə dinləyirlər. Heç bir öhdəlik götürməyə məcbur deyilsiniz.',
 4),
('Neçə seans lazım olacaq?',
 'Bu fərdə görə dəyişir. Bəzilərində 3-5 seans əhəmiyyətli fərq yaradır, digərləri üçün daha uzun müddət lazım ola bilər. Psixoloğunuz ilk görüşdən sonra təxmini bir plan təqdim edəcəkdir.',
 5),
('Onlayn seans effektivdirmi?',
 'Bəli, tədqiqatlar göstərir ki, onlayn psixoloji yardım üz-üzə seanslarla müqayisədə effektivlik baxımından demək olar ki, eynidir. Onlayn format əlavə olaraq rahatlıq, vaxt qənaəti və yerindən asılı olmama üstünlüklərini təqdim edir.',
 6);

-- Testimonials
INSERT INTO testimonials (quote, author_name, author_role, initials, gradient, rating) VALUES
('Fanus mənim həyatımı dəyişdi. İlk dəfə psixoloji yardım almaq qərarını qorxu ilə verdim, amma psixoloqla söhbətdən sonra özümü çox yüngül hiss etdim.',
 'Nigar Əliyeva', 'Marketinq Meneceri, 28', 'NƏ', 'linear-gradient(135deg, #3B6FA5, #5A4FC8)', 5),
('Stres idarəetmə texnikaları həyatımı tamamilə dəyişdirdi. 3 ay ərzində işimdəki performansım da, ailə münasibətlərim də yaxşılaşdı.',
 'Kamran Hüseynov', 'Mühəndis, 35', 'KH', 'linear-gradient(135deg, #1E7A6E, #0D9488)', 5),
('Ailə terapiyası sayəsində həyat yoldaşımla ünsiyyətimiz tamam fərqli bir səviyyəyə çıxdı. Tövsiyə edirəm.',
 'Aytən Babayeva', 'Müəllim, 42', 'AB', 'linear-gradient(135deg, #7C3AED, #A78BFA)', 5),
('Onlayn formatın bu qədər effektiv olacağını gözləmirdim. Evdən çıxmadan peşəkar dəstək almaq əla imkandır.',
 'Orxan Qasımov', 'Sahibkar, 31', 'OQ', 'linear-gradient(135deg, #D97706, #F59E0B)', 5),
('Depressiya dövründə Fanus ailəm kimi yanımda oldu. İndi özümü çox daha güclü hiss edirəm.',
 'Sevinc Muradova', 'Tələbə, 23', 'SM', 'linear-gradient(135deg, #3B6FA5, #0D9488)', 5),
('Psixoloq Elnur çox professional və empatikdir. Travmalarımla üzləşmək mümkün olduğunu ona görə bildim.',
 'Rauf Əsgərov', 'Hüquqşünas, 38', 'RƏ', 'linear-gradient(135deg, #1A5C8A, #5B4DA8)', 5),
('İlk seans üçün o qədər gərgin idim ki... Amma psixoloq Lalə məni elə rahat hiss etdirdi ki, növbəti randevunu həmin gün verdim.',
 'Günay İsmayılova', 'Həkim, 29', 'Gİ', 'linear-gradient(135deg, #1E7A6E, #7C3AED)', 5),
('Uşağımın ADHD diaqnozu bizi çox narahat edirdi. Tural müəllim bizə həm uşaqla, həm də özümüzlə işləməyi öyrətdi.',
 'Məryəm Hüseynova', 'Ev xanımı, 34', 'MH', 'linear-gradient(135deg, #A0522D, #D97706)', 5);

-- Site config
INSERT INTO site_config (config_key, config_value) VALUES
('phone',         '+994 50 123 45 67'),
('email',         'info@fanus.az'),
('working_hours', 'B.ertəsi – Şənbə, 09:00 – 20:00 · Onlayn 7/24');
