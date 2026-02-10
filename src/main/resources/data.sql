-- users テーブルに初期ユーザーを追加（status を追加）

INSERT INTO users (name, email, password, role, status, enabled, created_at)
VALUES ('一般ユーザー 太郎', 'customer@example.com', '{noop}pass1234', 'CUSTOMER', 'ACTIVE', true, CURRENT_TIMESTAMP);

INSERT INTO users (name, email, password, role, status, enabled, created_at)
VALUES ('スタッフ 花子', 'staff@example.com', '{noop}pass1234', 'STAFF', 'ACTIVE', true, CURRENT_TIMESTAMP);

INSERT INTO users (name, email, password, role, status, enabled, created_at)
VALUES ('管理者 管理', 'admin@example.com', '{noop}pass1234', 'ADMIN', 'ACTIVE', true, CURRENT_TIMESTAMP);

-- 商品データ
INSERT INTO items (name, description, price, stock, status, image_url, created_at)
VALUES ('太陽ニット', '明るいオレンジ色のニットセーター', 5980, 10, 'PUBLIC', '/images/a.png', CURRENT_TIMESTAMP);

INSERT INTO items (name, description, price, stock, status, image_url, created_at)
VALUES ('月光ワンピース', '夜のお出かけにもピッタリの黒ワンピース', 8980, 5, 'PUBLIC', '/images/a.png', CURRENT_TIMESTAMP);

INSERT INTO items (name, description, price, stock, status, image_url, created_at)
VALUES ('星屑パーカー', 'カジュアルな星柄パーカー', 4980, 0, 'SOLD', '/images/a.png', CURRENT_TIMESTAMP);