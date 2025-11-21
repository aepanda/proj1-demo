/*******************************************************************************
   Populate Tables
********************************************************************************/

/* -- USERS
INSERT INTO users (name, email, role) VALUES
('Sam DC Manager',   'sam.manager@hvparts.com',   'admin'),
('Iris Inventory',   'iris.inventory@hvparts.com','ops'),
('Liam Security',    'liam.security@hvparts.com', 'security');
*/

-- WAREHOUSES (Max capacity)
INSERT INTO warehouse (id, name, location, max_capacity) VALUES
   (101, 'Silicon Valley DC', 'San Jose, CA, USA', 50000),
   (102, 'Midwest DC', 'Chicago, IL, USA', 40000),
   (103, 'West Coast Fulfillment', 'Los Angeles, CA', 1000),
   (104, 'East Coast Hub', 'Newark, NJ', 800),
   (105, 'Overflow Storage', 'Austin, TX', 500);

-- SECURE LOCATIONS
INSERT INTO warehouse_shelf (id, code, description, warehouse_id) VALUES
   (201, 'CAGE-CPU-01', 'Secure CPU cage 101', 101),
   (202, 'CAGE-GPU-01', 'Secure GPU cage 101', 101),
   (203, 'RACK-A1',     'Standard rack A1', 102),
   (204, 'VAULT-SEN-1', 'Sensor vault 1', 103);

-- CATEGORIES
INSERT INTO category (id, name) VALUES
   (301, 'Microprocessors'),
   (302, 'Graphics Cards'),
   (303, 'Specialty Sensors'),
   (304, 'Memory'),
   (305, 'Storage'),
   (306, 'Power Supplies'),
   (307, 'Motherboards'),
   (308, 'Batteries');

-- PRODUCTS
INSERT INTO product
(id, name, sku, description, category_id) VALUES
   (401, 'Core i9-14900K Processor',
   'CPU-i9-14k',
   'High-performance desktop CPU.', 301),

   (402, 'GeForce RTX 4090 GPU',
   'GPU-4090-FE',
   'Flagship graphics card.', 302),

   (403, 'High-Accuracy Radar Sensor',
   'SENS-RAD-005',
   'Industrial radar module for automation.', 303),

   (404, '64GB DDR5 RAM Kit',
   'MEM-DDR5-64G',
   'High-speed memory module.', 304),

   (405, 'Samsung 990 Pro 2TB NVMe SSD', 'SSD-2TB-PRO', 'High-speed storage drive.', 305),

   (406, '1600W Platinum Power Supply', 
    'PSU-1600W-PLAT', 'High-wattage, high-efficiency PSU.', 306),

   (407, 'Z790 Motherboard Ultra', 'MBD-Z790-ULT', 'Top-tier desktop motherboard.', 307),

   (408, '25000mAh Lithium-Ion Battery Pack', 
    'BAT-LION-25000', 'High-density battery for industrial use.', 308);

-- INVENTORY (batches)
INSERT INTO inventory
(id, quantity_on_hand, expiration_date, warehouse_id,
 warehouse_shelf_id, product_id) VALUES
   (501, 500, NULL, 101, 201, 401),

   (502, 1200, NULL, 101, 202, 403),
  
   (503, 150, NULL, 102, 201, 402),

   (504, 3000, NULL, 102, 203, 405),
   
   (505, 800, NULL, 103, 204, 404);

-- SERIALIZED UNITS (subset of the EPYC and RTX)
INSERT INTO inventory_unit (id, serial_number, status, inventory_id) VALUES
   (601, 'EPYC-9654-SN-0001', 'AVAILABLE', 501),
   (602, 'EPYC-9654-SN-0002', 'AVAILABLE', 502),
   (603, 'EPYC-9654-SN-0003', 'RESERVED', 501),
   (604, 'RTX-A6000-SN-0101', 'AVAILABLE', 503),
   (605, 'RTX-A6000-SN-0102', 'AVAILABLE', 504),
   (606, 'RTX-A6000-SN-0103', 'SHIPPED', 501);

-- INITIAL RECEIPTS
INSERT INTO inventory_transaction
(id, product_id, from_warehouse_id, to_warehouse_id,
 from_shelf_id, to_shelf_id, inventory_id, quantity, transaction_type) VALUES
   (701, 401, NULL, 101, NULL, 201, 501, 500, 'INBOUND'),
   (702, 403, NULL, 101, NULL, 202, 501, 1200, 'INBOUND'),
   (703, 402, NULL, 102, NULL, 203, 502, 150, 'INBOUND'),
   (704, 404, NULL, 102, NULL, 203, 503, 3000, 'INBOUND'),
   (705, 405, NULL, 103, NULL, 204, 504, 800, 'INBOUND');

/*

-- TRANSFER (Silicon Valley -> Midwest, RTX GPUs)
INSERT INTO inventory_transfer
(product_id, source_warehouse_id, destination_warehouse_id,
 quantity, lot_number, status, requested_by, approved_by,
 created_at, completed_at) VALUES
   (3, 1, 2, 20, 'LOT-RTXA-2025-02', 'COMPLETED', 2, 1,
   NOW() - INTERVAL '3 days', NOW() - INTERVAL '2 days');

-- LOG TRANSFER MOVEMENT
INSERT INTO inventory_transaction
(product_id, from_warehouse_id, to_warehouse_id,
 from_location_id, to_location_id,
 inventory_id, quantity, type, reason, created_by) VALUES
   (3, 1, 2, 2, 3, NULL, 20, 'TRANSFER',
   'Transfer GPUs to Midwest DC for regional demand', 2);

-- ALERTS
INSERT INTO alert
(warehouse_id, product_id, inventory_id, type, severity, message) VALUES
   (2, 3, 4, 'OBSOLETING', 'WARNING',
   'RTX A6000 lot LOT-RTXA-2025-01 approaching EOL in 6 months'),
   (1, 1, 1, 'CAPACITY_NEAR_LIMIT', 'WARNING',
   'Silicon Valley CPU cage near capacity (EPYC inventory high)');

-- CAPACITY SNAPSHOTS
INSERT INTO warehouse_capacity_snapshot
(warehouse_id, snapshot_at, capacity_used_units, 
 capacity_percent, total_items, distinct_products) VALUES
   (1, NOW(), 22000, 44.00, 200, 2),
   (2, NOW(), 16000, 40.00, 100, 2),
   (3, NOW(),  8000, 17.78,  50, 1);

-- ACTIVITY LOG
INSERT INTO activity_log (user_id, entity_type, entity_id, action, details) VALUES
   (1, 'WAREHOUSE', 1, 'CREATE', '{"name":"Silicon Valley DC"}'),
   (1, 'PRODUCT',   1, 'CREATE', '{"sku":"CPU-EPYC-9654"}'),
   (2, 'TRANSFER',  1, 'CREATE', '{"product":"GPU-RTX-A6000","qty":20}'),
   (3, 'UNIT',      4, 'UPDATE', '{"serial":"RTX-A6000-SN-0103","status":"SHIPPED"}');

*/