**Project Overview**
- **Name:**: proj1-demo (Spring Boot backend for Proj1)
- **Purpose:**: REST API backend that manages inventory, warehouses and related domain logic for the web application. Use this README to get the project running locally, run tests, and build artifacts.

**Requirements**
- **Java:**: JDK 17+ (match the `pom.xml` Java version)
- **Build:**: Maven (project includes `mvnw` wrapper; use `./mvnw` when possible)
- **Database:**: PostgreSQL (local or container) — import `schema.sql` if provided

**Quick Start**
- **Clone repository:**

	`git clone https://github.com/aepanda/proj1-demo.git`
- **Configure environment variables:** set the database connection and any secrets before running. Example environment variables used by Spring Boot:

	- `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/proj1db`
	- `SPRING_DATASOURCE_USERNAME=proj1user`
	- `SPRING_DATASOURCE_PASSWORD=your_password`
	- `SPRING_PROFILES_ACTIVE=local`

- **Import DB schema (optional):**

	Use `psql` or your DB GUI to run the SQL in `proj1-demo/src/main/resources/schema.sql` (path may vary).

- **Run (development):**

	`./mvnw spring-boot:run`

- **Build JAR:**

	`./mvnw clean package`

	Then run:

	`java -jar target/*.jar`

**Configuration**
- **Spring config files:**: `src/main/resources/application.yml` (or `application.properties`). Prefer environment variables for credentials in development and CI.
- **Profiles:**: Use `SPRING_PROFILES_ACTIVE` to switch profiles (e.g., `local`, `dev`, `prod`).

**Database**
- **Default DB:**: PostgreSQL. Create a database and a user matching the `SPRING_DATASOURCE_*` values.
- **Seed/DDL:**: Check the `proj1-demo/src/main/resources/` folder for SQL scripts. Run them before the first application start.

**API Endpoints (extracted from controllers)**
- **Base paths:**
	- Warehouses: `/api/v1/warehouses`
	- Inventories: `/api/v1/inventories`

- **Warehouse endpoints**
	- `POST /api/v1/warehouses`
		- Create a new warehouse. Request body: JSON with `name`, `location`, `capacity`.
	- `GET /api/v1/warehouses`
		- List all warehouses.
	- `GET /api/v1/warehouses/id/{id}`
		- Get warehouse by ID.
	- `GET /api/v1/warehouses/dashboard`
		- Get dashboard metrics for all warehouses.
	- `GET /api/v1/warehouses/id/{id}/dashboard`
		- Get dashboard metrics for a single warehouse.
	- `PATCH /api/v1/warehouses/id/{id}`
		- Partial update a warehouse (accepts `UpdateWarehouseDTO` fields).
	- `GET /api/v1/warehouses/id/{id}/deletion-check`
		- Check whether a warehouse is eligible for deletion.
	- `DELETE /api/v1/warehouses/id/{id}`
		- Delete a warehouse (returns 204 on success).

- **Inventory endpoints**
	- `POST /api/v1/inventories`
		- Add a new inventory item (`AddInventoryItemDTO`).
	- `PATCH /api/v1/inventories/id/{inventoryId}`
		- Partial update inventory item (`UpdateInventoryItemDTO`).
	- `GET /api/v1/inventories/id/{inventoryId}/deletion-check`
		- Check whether an inventory item can be deleted.
	- `DELETE /api/v1/inventories/id/{inventoryId}`
		- Delete an inventory item (204 No Content on success).
	- `GET /api/v1/inventories/warehouses/id/{warehouseId}`
		- List all inventory items for a warehouse.
	- `GET /api/v1/inventories/warehouses/id/{warehouseId}/search/name?name=...`
		- Search inventory by product name (query param `name`).
	- `GET /api/v1/inventories/warehouses/id/{warehouseId}/search/sku?sku=...`
		- Search inventory by product SKU (query param `sku`).
	- `GET /api/v1/inventories/warehouses/id/{warehouseId}/filter/category?categoryId=...`
		- Filter inventory by category ID (query param `categoryId`).
	- `GET /api/v1/inventories/warehouses/id/{warehouseId}/search/advanced?name=...&sku=...&categoryId=...`
		- Advanced search with optional `name`, `sku`, and `categoryId` query params.
	- `POST /api/v1/inventories/transfer`
		- Transfer inventory between warehouses (`TransferInventoryRequestDTO`).

Note: controllers use `@CrossOrigin(origins = "http://localhost:5173")` for local frontend development. Update these endpoints or add authentication rules as needed.

**Testing**
- **Run unit/integration tests:**

	`./mvnw test`

**Docker (optional)**
- You can run PostgreSQL in Docker during development. Example:

	`docker run --name proj1-postgres -e POSTGRES_DB=proj1db -e POSTGRES_USER=proj1user -e POSTGRES_PASSWORD=your_password -p 5432:5432 -d postgres:15`

**Troubleshooting**
- **Failed DB connection:** Verify `SPRING_DATASOURCE_URL`, the database is reachable and credentials are correct. Check logs for full stacktrace.
- **Port in use:** Default Spring Boot port is `8080`; change with `server.port` property or use a different profile.
- **CORS issues:** If the front-end is separate, add CORS config or `@CrossOrigin` to controllers during local development.
- **Non-project file warnings in IDE:** Ensure your IDE recognizes the project root (`pom.xml`) and that the Java language level matches the JDK installed.

**Contributing**
- **Code style:** Follow existing code patterns and naming conventions. Keep changes small and focused.
- **Testing:** Add unit tests for new logic and run `./mvnw test` before creating a PR.

**Useful Commands**
- **Run app:** `./mvnw spring-boot:run`
- **Build:** `./mvnw clean package`
- **Test:** `./mvnw test`


