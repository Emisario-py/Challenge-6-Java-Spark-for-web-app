-- ==========================================
--  Archivo: seed-items.sql
--  Propósito: Insertar items por defecto sin usuario asignado
--  Proyecto: DigitalNao
-- ==========================================

-- Limpia los registros previos (solo para desarrollo)
DELETE FROM items;

-- Inserta ítems base sin usuario asignado
INSERT INTO items (name, description, user_id, initialPrice) VALUES
                                                   ('Laptop Dell XPS 13', 'Ultrabook de 13 pulgadas con pantalla táctil', NULL, 100 ),
                                                   ('Teclado Mecánico RGB', 'Teclado con switches rojos y retroiluminación', NULL, 100),
                                                   ('Mouse Inalámbrico Logitech', 'Mouse ergonómico con conexión Bluetooth', NULL, 100),
                                                   ('Monitor Samsung 27"', 'Pantalla LED de 27 pulgadas Full HD', NULL, 100),
                                                   ('Auriculares Sony WH-1000XM4', 'Audífonos con cancelación de ruido y micrófono integrado', NULL, 100),
                                                   ('Base de Carga USB-C', 'Dock de carga universal con salida rápida de 65W', NULL, 100),
                                                   ('Laptop HP Envy', 'Laptop con procesador Intel i7 y SSD de 512GB', NULL, 100),
                                                   ('Tablet Samsung Galaxy Tab S7', 'Pantalla AMOLED de 11 pulgadas y S-Pen incluido', NULL, 100),
                                                   ('Impresora Epson EcoTank', 'Impresora multifuncional con sistema de tinta continua', NULL, 100),
                                                   ('Cámara Canon EOS M50', 'Cámara mirrorless con lente intercambiable y grabación en 4K', NULL, 100),
                                                   ('Smartwatch Garmin Venu 2', 'Reloj inteligente con GPS y monitoreo de salud', NULL, 100),
                                                   ('Disco Duro Externo Seagate', 'Almacenamiento portátil de 2TB con conexión USB 3.0', NULL, 100),
                                                   ('Hub USB 3.0', 'Concentrador con 4 puertos USB de alta velocidad', NULL, 100),
                                                   ('Router TP-Link Archer AX20', 'Router Wi-Fi 6 de doble banda con 4 antenas externas', NULL, 100),
                                                   ('Soporte para Laptop', 'Base ajustable de aluminio para computadora portátil', NULL, 100),
                                                   ('Micrófono Blue Yeti', 'Micrófono USB profesional ideal para streaming o grabación', NULL, 100),
                                                   ('Altavoz Bluetooth JBL Flip 6', 'Bocina portátil resistente al agua con batería de 12 horas', NULL, 100),
                                                   ('Webcam Logitech C920', 'Cámara web Full HD 1080p con micrófono integrado', NULL, 100),
                                                   ('Cargador GaN Anker 65W', 'Cargador rápido con dos puertos USB-C y uno USB-A', NULL, 100),
                                                   ('SSD NVMe Samsung 980 1TB', 'Unidad de estado sólido con velocidad de lectura de 3500MB/s', NULL, 100);


