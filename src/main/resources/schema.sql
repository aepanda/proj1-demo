/*******************************************************************************
   Project1 Database - Version 1.0.0
   Script: Proj1_PostgreSql.sql
   Description: Create and Populate data for a 
      High-Value Electronic Parts Distributor Inventory Management Service.
   DB Server: PostgreSql
   Author: Arnold Behl Epanda
   License: https://github.com/aepanda/project1-demo-database/blob/main/LICENSE.md
********************************************************************************/


/*******************************************************************************
   Drop Tables
********************************************************************************/

-- Drop in FK-safe order (children first)
DROP TABLE IF EXISTS activity_log                  CASCADE;
DROP TABLE IF EXISTS warehouse_capacity_snapshot   CASCADE;
DROP TABLE IF EXISTS alert                         CASCADE;
DROP TABLE IF EXISTS inventory_transfer            CASCADE;
DROP TABLE IF EXISTS inventory_transaction         CASCADE;
DROP TABLE IF EXISTS inventory_unit                CASCADE;
DROP TABLE IF EXISTS inventory                     CASCADE;
DROP TABLE IF EXISTS product                       CASCADE;
DROP TABLE IF EXISTS category                      CASCADE;
DROP TABLE IF EXISTS warehouse_shelf               CASCADE;
DROP TABLE IF EXISTS warehouse                     CASCADE;
-- DROP TABLE IF EXISTS users                         CASCADE;



/*******************************************************************************
   Create Tables
********************************************************************************/ 

/* -- USERS
CREATE TABLE user (
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    email       TEXT NOT NULL UNIQUE,
    role        TEXT NOT NULL DEFAULT 'admin', -- 'admin','ops','security','viewer'
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
); 
*/

-- WAREHOUSES
CREATE TABLE warehouse 
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR(255) NOT NULL UNIQUE,
    location            VARCHAR(255) NOT NULL,
    max_capacity        INT NOT NULL CHECK (max_capacity > 0), -- abstract capacity points (volume/weight)
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ DEFAULT NOW(),
    updated_at          TIMESTAMPTZ 
);

-- INTERNAL LOCATIONS (Racks / Bins / Shelves)
CREATE TABLE warehouse_shelf 
(
    id           SERIAL PRIMARY KEY,
    code         TEXT NOT NULL,
    description  TEXT,
    created_at   TIMESTAMPTZ DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    warehouse_id INT NOT NULL,
    CONSTRAINT uq_location_per_warehouse UNIQUE (warehouse_id, code),
    CONSTRAINT warehouse_id_fk 
         FOREIGN KEY (warehouse_id) REFERENCES warehouse (id) ON DELETE CASCADE
);

-- PRODUCT CATEGORIES
CREATE TABLE category 
(
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    description TEXT
);

-- PRODUCTS (high-value electronics)
CREATE TABLE product 
(
    id               SERIAL PRIMARY KEY,
    name             TEXT NOT NULL,
    sku              TEXT NOT NULL UNIQUE,
    description      TEXT,
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ DEFAULT NOW(),
    updated_at       TIMESTAMPTZ,
    category_id      INT,
    CONSTRAINT category_id_fk 
         FOREIGN KEY (category_id) REFERENCES category (id)
);

-- BULK INVENTORY (batch)
CREATE TABLE inventory 
(
    id                    SERIAL PRIMARY KEY,
    quantity_on_hand      INT NOT NULL DEFAULT 0,
    expiration_date       DATE,            -- can be used as "last use by" 
    created_at            TIMESTAMPTZ DEFAULT NOW(),
    updated_at            TIMESTAMPTZ,
    warehouse_id          INT NOT NULL,
    warehouse_shelf_id    INT,
    product_id            INT NOT NULL,
    CONSTRAINT uq_inventory_line UNIQUE 
         (warehouse_id, warehouse_shelf_id, product_id, expiration_date),
    CONSTRAINT warehouse_id_fk 
         FOREIGN KEY (warehouse_id) REFERENCES warehouse (id) ON DELETE RESTRICT,
    CONSTRAINT warehouse_shelf_id_fk
         FOREIGN KEY (warehouse_shelf_id) REFERENCES warehouse_shelf (id) ON DELETE SET NULL,
    CONSTRAINT product_id_fk
         FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT
    
);

CREATE INDEX idx_inventory_warehouse  ON inventory (warehouse_id);
CREATE INDEX idx_inventory_product    ON inventory (product_id);
CREATE INDEX idx_inventory_expiration ON inventory (expiration_date);

-- SERIALIZED UNITS (for top-tier parts)
CREATE TABLE inventory_unit 
(
    id             SERIAL PRIMARY KEY,
    serial_number  TEXT NOT NULL,
    status         TEXT NOT NULL DEFAULT 'AVAILABLE', -- 'AVAILABLE','RESERVED','SHIPPED'
    created_at     TIMESTAMPTZ DEFAULT NOW(),
    updated_at     TIMESTAMPTZ,
    inventory_id   INT NOT NULL,
    CONSTRAINT uq_serial UNIQUE (serial_number),
    CONSTRAINT inventory_id_fk
         FOREIGN KEY (inventory_id) REFERENCES inventory (id) ON DELETE CASCADE
);

CREATE INDEX idx_inventory_unit_status ON inventory_unit(status);

-- TRANSACTION LOG
-- NOTE: inventory_id removed - can be derived from warehouse + shelf + product combination
CREATE TABLE inventory_transaction 
(
    id                 SERIAL PRIMARY KEY,
    quantity           INT NOT NULL,
    transaction_type   TEXT NOT NULL,  -- 'INBOUND','OUTBOUND','TRANSFER','ADJUSTMENT'
    -- created_by         INT REFERENCES user (id),
    created_at         TIMESTAMPTZ DEFAULT NOW(),
    product_id         INT NOT NULL,
    from_warehouse_id  INT,
    to_warehouse_id    INT,
    from_shelf_id      INT,
    to_shelf_id        INT,
    CONSTRAINT product_id_fk
         FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT,
    CONSTRAINT from_warehouse_id_fk
         FOREIGN KEY (from_warehouse_id) REFERENCES warehouse (id) ON DELETE RESTRICT,
    CONSTRAINT to_warehouse_id_fk
         FOREIGN KEY (to_warehouse_id) REFERENCES warehouse (id) ON DELETE RESTRICT,
    CONSTRAINT from_shelf_id_fk
         FOREIGN KEY (from_shelf_id) REFERENCES warehouse_shelf (id) ON DELETE SET NULL,
    CONSTRAINT to_shelf_id_fk
         FOREIGN KEY (to_shelf_id) REFERENCES warehouse_shelf (id) ON DELETE SET NULL
);

CREATE INDEX idx_tx_product    ON inventory_transaction (product_id);
CREATE INDEX idx_tx_created_at ON inventory_transaction (created_at);

-- TRANSFER WORKFLOW
CREATE TABLE inventory_transfer 
(
    id                       SERIAL PRIMARY KEY,
    quantity                 INT NOT NULL,
    status                   TEXT NOT NULL DEFAULT 'PENDING', -- 'PENDING','IN_TRANSIT','COMPLETED','CANCELLED'
    -- requested_by             INT REFERENCES user (id),
    -- approved_by              INT REFERENCES user (id),
    created_at               TIMESTAMPTZ DEFAULT NOW(),
    completed_at             TIMESTAMPTZ,
    product_id               INT NOT NULL,
    source_warehouse_id      INT NOT NULL,
    destination_warehouse_id INT NOT NULL,
    CONSTRAINT product_id_fk
         FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT,
    CONSTRAINT source_warehouse_id_fk
         FOREIGN KEY (source_warehouse_id) REFERENCES warehouse (id) ON DELETE RESTRICT,
    CONSTRAINT destination_warehouse_id_fk
         FOREIGN KEY (destination_warehouse_id) REFERENCES warehouse (id) ON DELETE RESTRICT
);

-- ALERTS (capacity, loss risk, obsolescence)
-- NOTE: product_id removed - access product via inventory.product relationship
CREATE TABLE alert 
(
    id            SERIAL PRIMARY KEY,
    type          TEXT NOT NULL,   -- 'CAPACITY_NEAR_LIMIT','CAPACITY_EXCEEDED','OBSOLETE','LOSS_RISK'
    severity      TEXT NOT NULL DEFAULT 'INFO', -- 'INFO','WARNING','CRITICAL'
    is_resolved   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ DEFAULT NOW(),
    resolved_at   TIMESTAMPTZ,
    -- resolved_by   INT REFERENCES user (id),
    warehouse_id  INT,
    inventory_id  INT,
    CONSTRAINT warehouse_id_fk
         FOREIGN KEY (warehouse_id) REFERENCES warehouse (id) ON DELETE CASCADE,
    CONSTRAINT inventory_id_fk
         FOREIGN KEY (inventory_id) REFERENCES inventory (id) ON DELETE CASCADE,
    CONSTRAINT chk_alert_has_reference
         CHECK ((warehouse_id IS NOT NULL) OR (inventory_id IS NOT NULL))
);

CREATE INDEX idx_alert_warehouse   ON alert (warehouse_id);
CREATE INDEX idx_alert_type        ON alert (type);
CREATE INDEX idx_alert_created_at  ON alert (created_at);

-- CAPACITY SNAPSHOTS
CREATE TABLE warehouse_capacity_snapshot 
(
    id                    SERIAL PRIMARY KEY,
    snapshot_at           TIMESTAMPTZ DEFAULT NOW(),
    capacity_used_units   INT NOT NULL,
    capacity_percent      INT NOT NULL,
    total_items           INT NOT NULL,
    warehouse_id          INT NOT NULL,
    CONSTRAINT uq_snapshot UNIQUE (warehouse_id, snapshot_at),
    CONSTRAINT warehouse_id_fk
         FOREIGN KEY (warehouse_id) REFERENCES warehouse (id) ON DELETE CASCADE
);

CREATE INDEX idx_snapshot_warehouse ON warehouse_capacity_snapshot (warehouse_id);
CREATE INDEX idx_snapshot_at        ON warehouse_capacity_snapshot (snapshot_at);

-- ACTIVITY LOG
-- Composite unique constraint using id + entity_type + action prevents duplicate audit entries
CREATE TABLE activity_log 
(
    id            SERIAL PRIMARY KEY,
    -- user_id       INT REFERENCES user (id),
    entity_id     INT NOT NULL,
    entity_type   TEXT NOT NULL, -- 'WAREHOUSE','INVENTORY','PRODUCT','ALERT'
    action        TEXT NOT NULL, -- 'CREATE','UPDATE','DELETE'
    created_at    TIMESTAMPTZ DEFAULT NOW(),
    updated_at    TIMESTAMPTZ,
    deleted_at    TIMESTAMPTZ,

    -- Composite unique constraint: prevents duplicate (entity_type, entity_id, action) combinations
    -- CONSTRAINT uq_entity_action UNIQUE (entity_type, entity_id, action),
    -- Changed: Added timestamp to allow multiple updates per entity

    -- Validates entity_type values
    CONSTRAINT chk_valid_entity_type 
        CHECK (entity_type IN ('WAREHOUSE','INVENTORY','PRODUCT','ALERT')),
    
    -- Validates action values
    CONSTRAINT chk_valid_action
        CHECK (action IN ('CREATE','UPDATE','DELETE'))
);

CREATE INDEX idx_activity_entity ON activity_log (entity_type);
CREATE INDEX idx_activity_created_at ON activity_log (created_at);
CREATE INDEX idx_activity_action ON activity_log (action);


