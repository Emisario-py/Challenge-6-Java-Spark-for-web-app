-- ==========================================
--  Archivo: seed-items.sql
--  Propósito: Insertar items por defecto sin usuario asignado
--  Proyecto: DigitalNao
-- ==========================================

-- Limpia los registros previos (solo para desarrollo)
DELETE FROM items;

-- Inserta ítems base sin usuario asignado
INSERT INTO items (name, description, user_id) VALUES
                                                   ('Laptop Dell XPS 13', 'Ultrabook de 13 pulgadas con pantalla táctil', NULL),
                                                   ('Teclado Mecánico RGB', 'Teclado con switches rojos y retroiluminación', NULL),
                                                   ('Mouse Inalámbrico Logitech', 'Mouse ergonómico con conexión Bluetooth', NULL),
                                                   ('Monitor Samsung 27"', 'Pantalla LED de 27 pulgadas Full HD', NULL),
                                                   ('Auriculares Sony WH-1000XM4', 'Audífonos con cancelación de ruido y micrófono integrado', NULL),
                                                   ('Base de Carga USB-C', 'Dock de carga universal con salida rápida de 65W', NULL),
                                                   ('Laptop HP Envy', 'Laptop con procesador Intel i7 y SSD de 512GB', NULL),
                                                   ('Tablet Samsung Galaxy Tab S7', 'Pantalla AMOLED de 11 pulgadas y S-Pen incluido', NULL),
                                                   ('Impresora Epson EcoTank', 'Impresora multifuncional con sistema de tinta continua', NULL),
                                                   ('Cámara Canon EOS M50', 'Cámara mirrorless con lente intercambiable y grabación en 4K', NULL),
                                                   ('Smartwatch Garmin Venu 2', 'Reloj inteligente con GPS y monitoreo de salud', NULL),
                                                   ('Disco Duro Externo Seagate', 'Almacenamiento portátil de 2TB con conexión USB 3.0', NULL),
                                                   ('Hub USB 3.0', 'Concentrador con 4 puertos USB de alta velocidad', NULL),
                                                   ('Router TP-Link Archer AX20', 'Router Wi-Fi 6 de doble banda con 4 antenas externas', NULL),
                                                   ('Soporte para Laptop', 'Base ajustable de aluminio para computadora portátil', NULL),
                                                   ('Micrófono Blue Yeti', 'Micrófono USB profesional ideal para streaming o grabación', NULL),
                                                   ('Altavoz Bluetooth JBL Flip 6', 'Bocina portátil resistente al agua con batería de 12 horas', NULL),
                                                   ('Webcam Logitech C920', 'Cámara web Full HD 1080p con micrófono integrado', NULL),
                                                   ('Cargador GaN Anker 65W', 'Cargador rápido con dos puertos USB-C y uno USB-A', NULL),
                                                   ('SSD NVMe Samsung 980 1TB', 'Unidad de estado sólido con velocidad de lectura de 3500MB/s', NULL);

-- Confirmación visual (opcional)
SELECT COUNT(*) AS total_items, 'Items insertados correctamente' AS mensaje FROM items;
