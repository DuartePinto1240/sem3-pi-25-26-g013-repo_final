SET
DEFINE OFF

PROMPT ==== 1) OWNERS ====
-- Inserts from Excel: Owner -> table Owner
MERGE INTO Owner t USING (SELECT 'Infraestruturas de Portugal, SA' name, 'IP' short_name, 'PT503933813' vat_number FROM dual) s
ON (t.vat_number=s.vat_number)
WHEN NOT MATCHED THEN INSERT (name, short_name, vat_number) VALUES (s.name, s.short_name, s.vat_number);


PROMPT
==== 2) OPERATORS ====
-- Inserts from Excel: Operator -> table Operator
MERGE INTO Operator t USING (SELECT 'Medway - Operador Ferroviário de Mercadorias, S.A' name, 'Medway' short_name, 'PT509017800' vat_number, 'PT' country FROM dual) s
ON (t.vat_number=s.vat_number)
WHEN NOT MATCHED THEN INSERT (name, short_name, vat_number, country) VALUES (s.name, s.short_name, s.vat_number, s.country);


PROMPT
==== 3) FACILITIES ====
-- Inserts from Excel: Facility -> tables PostalCode, Address, Facility
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-1' street, 'Unknown' city, 'PT' country FROM dual) s
ON (t.street=s.street)
WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-1') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'São Romão' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-1'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-2' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-2') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Tamel' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-2'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-3' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-3') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Senhora das Dores' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-3'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-4' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-4') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Lousado' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-4'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-5' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-5') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Porto Campanhã' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-5'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-6' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-6') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Leandro' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-6'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-7' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-7') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Porto São Bento' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-7'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-8' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-8') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Barcelos' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-8'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-9' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-9') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Vila Nova da Cerveira' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-9'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-10' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-10') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Midões' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-10'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-11' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-11') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Valença' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-11'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-12' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-12') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Darque' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-12'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-13' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-13') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Contumil' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-13'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-14' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-14') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Ermesinde' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-14'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-15' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-15') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'São Frutuoso' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-15'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-16' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-16') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'São Pedro da Torre' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-16'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-17' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-17') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Viana do Castelo' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-17'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-18' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-18') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Famalicão' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-18'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-19' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-19') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Barroselas' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-19'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-20' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-20') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Nine' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-20'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-21' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-21') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Caminha' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-21'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-22' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-22') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Carvalha' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-22'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);
MERGE INTO PostalCode t USING (SELECT 'AUTO-FACILITY-23' street, 'Unknown' city, 'PT' country
                               FROM dual) s
    ON (t.street = s.street)
    WHEN NOT MATCHED THEN INSERT (street, city, country) VALUES (s.street, s.city, s.country);
MERGE INTO Address t USING (SELECT p.postalCode_id postalCode_id, 0 doorNumber
                            FROM PostalCode p
                            WHERE p.street = 'AUTO-FACILITY-23') s ON (t.postalCode_id = s.postalCode_id AND t.doorNumber = s.doorNumber)
    WHEN NOT MATCHED THEN INSERT (postalCode_id, doorNumber) VALUES (s.postalCode_id, s.doorNumber);
MERGE INTO Facility t USING (SELECT a.address_id address_id, 'Station' type, 'Carreço' name
                             FROM Address a
                                      JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
                             WHERE p.street = 'AUTO-FACILITY-23'
                               AND a.doorNumber = 0) s ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (address_id, type, name) VALUES (s.address_id, s.type, s.name);


PROMPT
==== 4) STATIONS ====
INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
WHERE f.type = 'Station'
  AND NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

PROMPT
==== 5) GAUGES & FlUEL TYPES ====

DECLARE
v_count NUMBER;
BEGIN
SELECT COUNT(*)
INTO v_count
FROM user_tables
WHERE table_name = 'FLUELTYPE';
IF
v_count = 0 THEN
    EXECUTE IMMEDIATE q'[
      CREATE TABLE FluelType
      (
          flueltype_id number(10) GENERATED AS IDENTITY,
          type        varchar2(8) NOT NULL,
          PRIMARY KEY (flueltype_id),
          CONSTRAINT CHK_flueltype_valid CHECK (type IN ('Diesel', 'Electric'))
      )
    ]';
END IF;
END;
/

MERGE INTO Gauge t USING (SELECT 1668 width_mm FROM dual) s
  ON (t.width_mm=s.width_mm)
WHEN NOT MATCHED THEN INSERT (width_mm, description) VALUES (s.width_mm,'AUTO');
MERGE INTO Gauge t USING (SELECT 1435 width_mm
                          FROM dual) s
    ON (t.width_mm = s.width_mm)
    WHEN NOT MATCHED THEN INSERT (width_mm, description) VALUES (s.width_mm, 'AUTO');

MERGE INTO FluelType t USING (SELECT 'Diesel' type
                              FROM dual
                              UNION ALL
                              SELECT 'Electric'
                              FROM dual) s
    ON (t.type = s.type)
    WHEN NOT MATCHED THEN INSERT (type) VALUES (s.type);

PROMPT
==== 6) RAILWAY LINES (original + nomes corrigidos) ====
-- Garantir que qualquer 'Camapanhã' existente é normalizado
UPDATE RailwayLine
SET name='Ramal Campanhã - Nine'
WHERE name = 'Ramal Camapanhã - Nine';
-- Inserts from Excel: Line -> tables Gauge, Station (for endpoints), RailwayLine

-- Garante a Gauge 1668 (OK se já existir: 0 rows merged)
MERGE INTO Gauge t USING (SELECT 1668 width_mm
                          FROM dual) s
    ON (t.width_mm = s.width_mm)
    WHEN NOT MATCHED THEN INSERT (width_mm, description) VALUES (s.width_mm, 'AUTO');

-- *** NOVO BLOCO: cria Station para TODAS as Facilities (evita 0 rows e NULL nos endpoints) ***
INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
WHERE NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

-- (mantive as tuas inserções de Station com street; se não casarem, apenas não inserem nada)
INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
         JOIN Address a ON f.address_id = a.address_id
         JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
WHERE p.street = 'AUTO-FACILITY-5'
  AND NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
         JOIN Address a ON f.address_id = a.address_id
         JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
WHERE p.street = 'AUTO-FACILITY-7'
  AND NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
         JOIN Address a ON f.address_id = a.address_id
         JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
WHERE p.street = 'AUTO-FACILITY-8'
  AND NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
         JOIN Address a ON f.address_id = a.address_id
         JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
WHERE p.street = 'AUTO-FACILITY-11'
  AND NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
         JOIN Address a ON f.address_id = a.address_id
         JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
WHERE p.street = 'AUTO-FACILITY-16'
  AND NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
         JOIN Address a ON f.address_id = a.address_id
         JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
WHERE p.street = 'AUTO-FACILITY-17'
  AND NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
         JOIN Address a ON f.address_id = a.address_id
         JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
WHERE p.street = 'AUTO-FACILITY-20'
  AND NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

INSERT INTO Station (facility_id)
SELECT f.facility_id
FROM Facility f
         JOIN Address a ON f.address_id = a.address_id
         JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
WHERE p.street = 'AUTO-FACILITY-21'
  AND NOT EXISTS (SELECT 1 FROM Station s WHERE s.facility_id = f.facility_id);

-- *** CORREÇÃO: vírgula entre as subqueries de owner_id e gauge_id ***

INSERT INTO RailwayLine (name, owner_id, gauge_id, start_station_id, end_station_id)
SELECT 'Ramal São Bento - Campanhã',
       (SELECT owner_id FROM Owner WHERE short_name = 'IP'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-7'),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-5')
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM RailwayLine WHERE name = 'Ramal São Bento - Campanhã');

INSERT INTO RailwayLine (name, owner_id, gauge_id, start_station_id, end_station_id)
SELECT 'Ramal Campanhã - Nine',
       (SELECT owner_id FROM Owner WHERE short_name = 'IP'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-5'),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-20')
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM RailwayLine WHERE name = 'Ramal Campanhã - Nine');

INSERT INTO RailwayLine (name, owner_id, gauge_id, start_station_id, end_station_id)
SELECT 'Ramal Nine - Barcelos',
       (SELECT owner_id FROM Owner WHERE short_name = 'IP'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-20'),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-8')
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM RailwayLine WHERE name = 'Ramal Nine - Barcelos');

INSERT INTO RailwayLine (name, owner_id, gauge_id, start_station_id, end_station_id)
SELECT 'Ramal Barcelos - Viana',
       (SELECT owner_id FROM Owner WHERE short_name = 'IP'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-8'),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-17')
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM RailwayLine WHERE name = 'Ramal Barcelos - Viana');

INSERT INTO RailwayLine (name, owner_id, gauge_id, start_station_id, end_station_id)
SELECT 'Ramal Viana - Caminha',
       (SELECT owner_id FROM Owner WHERE short_name = 'IP'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-17'),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-21')
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM RailwayLine WHERE name = 'Ramal Viana - Caminha');

INSERT INTO RailwayLine (name, owner_id, gauge_id, start_station_id, end_station_id)
SELECT 'Ramal Caminha - São Pedro da Torre',
       (SELECT owner_id FROM Owner WHERE short_name = 'IP'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-21'),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-16')
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM RailwayLine WHERE name = 'Ramal Caminha - São Pedro da Torre');

INSERT INTO RailwayLine (name, owner_id, gauge_id, start_station_id, end_station_id)
SELECT 'Ramal São Pedro da Torre - Valença',
       (SELECT owner_id FROM Owner WHERE short_name = 'IP'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-16'),
       (SELECT s.station_id
        FROM Station s
                 JOIN Facility f ON s.facility_id = f.facility_id
                 JOIN Address a ON f.address_id = a.address_id
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FACILITY-11')
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM RailwayLine WHERE name = 'Ramal São Pedro da Torre - Valença');


PROMPT
==== 7) LINE SEGMENTS (corrigidos, substituem os originais) ====

-- Replaced original segment inserts with name-based, FK-safe inserts
DELETE
FROM LineSegment; -- reset segments to avoid dupes/inconsistent FK targeting

INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal São Bento - Campanhã'),
       2618,
       120,
       8000,
       1,
       'double',
       'Yes'
FROM dual;

INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Campanhã - Nine'),
       29003,
       120,
       8000,
       1,
       'double',
       'Yes'
FROM dual;
INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Campanhã - Nine'),
       10000,
       120,
       8000,
       2,
       'double',
       'Yes'
FROM dual;

INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Nine - Barcelos'),
       5286,
       120,
       8000,
       1,
       'double',
       'Yes'
FROM dual;
INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Nine - Barcelos'),
       6000,
       120,
       8000,
       2,
       'double',
       'Yes'
FROM dual;

INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Barcelos - Viana'),
       10387,
       120,
       8000,
       1,
       'double',
       'Yes'
FROM dual;
INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Barcelos - Viana'),
       12000,
       120,
       8000,
       2,
       'double',
       'Yes'
FROM dual;
INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Barcelos - Viana'),
       8000,
       120,
       6400,
       3,
       'double',
       'Yes'
FROM dual;

INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Viana - Caminha'),
       6000,
       120,
       8000,
       1,
       'double',
       'Yes'
FROM dual;
INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Viana - Caminha'),
       3000,
       120,
       8000,
       2,
       'double',
       'Yes'
FROM dual;
INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Viana - Caminha'),
       15000,
       120,
       8000,
       3,
       'double',
       'Yes'
FROM dual;

INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal Caminha - São Pedro da Torre'),
       20829,
       120,
       8000,
       1,
       'double',
       'Yes'
FROM dual;

INSERT INTO LineSegment (line_id, length_m, speed_limit, max_weight, segment_order, track_type, electrified)
SELECT (SELECT line_id FROM RailwayLine WHERE name = 'Ramal São Pedro da Torre - Valença'),
       4264,
       120,
       8000,
       1,
       'double',
       'Yes'
FROM dual;


PROMPT
==== 8) WAGON TYPES & MODELS ====
-- Inserts from Excel: Wagon Model -> tables WagonType, Gauge, WagonModel
MERGE INTO WagonType t USING (SELECT 'Cereal wagon' type FROM dual) s
ON (t.type=s.type)
WHEN NOT MATCHED THEN INSERT (type) VALUES (s.type);
MERGE INTO WagonType t USING (SELECT 'Container wagon (max 40'''' HC)' type
                              FROM dual) s
    ON (t.type = s.type)
    WHEN NOT MATCHED THEN INSERT (type) VALUES (s.type);
MERGE INTO WagonType t USING (SELECT 'Covered wagon with sliding door' type
                              FROM dual) s
    ON (t.type = s.type)
    WHEN NOT MATCHED THEN INSERT (type) VALUES (s.type);
MERGE INTO Gauge t USING (SELECT 1435 width_mm
                          FROM dual) s ON (t.width_mm = s.width_mm)
    WHEN NOT MATCHED THEN INSERT (width_mm, description) VALUES (s.width_mm, 'AUTO');
MERGE INTO Gauge t USING (SELECT 1668 width_mm
                          FROM dual) s ON (t.width_mm = s.width_mm)
    WHEN NOT MATCHED THEN INSERT (width_mm, description) VALUES (s.width_mm, 'AUTO');
MERGE INTO WagonModel t
    USING (SELECT 'Tadgs 32 94 082 3' name
           FROM dual) s
    ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (
                                  name, manufacturer, wagontype_id, gauge_id, axles, decks, length_m, tare_t, payload_t,
                                  max_speed_loaded_kmh, max_speed_empty_kmh
        ) VALUES ('Tadgs 32 94 082 3', 'Metalsines',
                  (SELECT wagontype_id FROM WagonType WHERE type = 'Cereal wagon'),
                  (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
                  2, 2, 17240, 24, 56, 120, 120);
MERGE INTO WagonModel t
    USING (SELECT 'Tdgs 41 94 074 1' name
           FROM dual) s
    ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (
                                  name, manufacturer, wagontype_id, gauge_id, axles, decks, length_m, tare_t, payload_t,
                                  max_speed_loaded_kmh, max_speed_empty_kmh
        ) VALUES ('Tdgs 41 94 074 1', 'Equimetal',
                  (SELECT wagontype_id FROM WagonType WHERE type = 'Cereal wagon'),
                  (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
                  2, 2, 9640, 13, 26, 100, 100);
MERGE INTO WagonModel t
    USING (SELECT 'Gabs 81 94 181 1' name
           FROM dual) s
    ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (
                                  name, manufacturer, wagontype_id, gauge_id, axles, decks, length_m, tare_t, payload_t,
                                  max_speed_loaded_kmh, max_speed_empty_kmh
        ) VALUES ('Gabs 81 94 181 1', 'Sepsa Cometna',
                  (SELECT wagontype_id FROM WagonType WHERE type = 'Covered wagon with sliding door'),
                  (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
                  2, 2, 21700, 29, 50, 100, 100);
MERGE INTO WagonModel t
    USING (SELECT 'Regmms 32 94 356 3' name
           FROM dual) s
    ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (
                                  name, manufacturer, wagontype_id, gauge_id, axles, decks, length_m, tare_t, payload_t,
                                  max_speed_loaded_kmh, max_speed_empty_kmh
        ) VALUES ('Regmms 32 94 356 3', 'Metalsines',
                  (SELECT wagontype_id FROM WagonType WHERE type = 'Container wagon (max 40'''' HC)'),
                  (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
                  2, 2, 14040, 21, 60, 120, 120);
MERGE INTO WagonModel t
    USING (SELECT 'Lgs 22 94 441 6' name
           FROM dual) s
    ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (
                                  name, manufacturer, wagontype_id, gauge_id, axles, decks, length_m, tare_t, payload_t,
                                  max_speed_loaded_kmh, max_speed_empty_kmh
        ) VALUES ('Lgs 22 94 441 6', 'Metalsines',
                  (SELECT wagontype_id FROM WagonType WHERE type = 'Container wagon (max 40'''' HC)'),
                  (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
                  1, 1, 13860, 11, 28, 120, 120);
MERGE INTO WagonModel t
    USING (SELECT 'Sgnss 12 94 455 2' name
           FROM dual) s
    ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (
                                  name, manufacturer, wagontype_id, gauge_id, axles, decks, length_m, tare_t, payload_t,
                                  max_speed_loaded_kmh, max_speed_empty_kmh
        ) VALUES ('Sgnss 12 94 455 2', 'Emef',
                  (SELECT wagontype_id FROM WagonType WHERE type = 'Container wagon (max 40'''' HC)'),
                  (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
                  2, 2, 18116, 21, 68, 120, 120);
MERGE INTO WagonModel t
    USING (SELECT 'Sgnss 12 94 455 2' name
           FROM dual) s
    ON (t.name = s.name)
    WHEN NOT MATCHED THEN INSERT (
                                  name, manufacturer, wagontype_id, gauge_id, axles, decks, length_m, tare_t, payload_t,
                                  max_speed_loaded_kmh, max_speed_empty_kmh
        ) VALUES ('Sgnss 12 94 455 2', 'Emef',
                  (SELECT wagontype_id FROM WagonType WHERE type = 'Container wagon (max 40'''' HC)'),
                  (SELECT gauge_id FROM Gauge WHERE width_mm = 1435),
                  2, 2, 18116, 21, 68, 120, 120);


PROMPT
==== 9) WAGONS (original com join corrigido) ====
-- Inserts from Excel: Wagon -> table Wagon
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Regmms 32 94 356 3'),
       1987
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tadgs 32 94 082 3'),
       1990
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tadgs 32 94 082 3'),
       1990
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tadgs 32 94 082 3'),
       1990
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tadgs 32 94 082 3'),
       1990
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tdgs 41 94 074 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tdgs 41 94 074 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tdgs 41 94 074 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tdgs 41 94 074 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tdgs 41 94 074 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Tdgs 41 94 074 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Gabs 81 94 181 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Gabs 81 94 181 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Gabs 81 94 181 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Gabs 81 94 181 1'),
       1977
FROM dual;
INSERT INTO Wagon (operator_id, wagonmodel_id, service_year)
SELECT (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       (SELECT wagonmodel_id FROM WagonModel WHERE name = 'Gabs 81 94 181 1'),
       1977
FROM dual;


PROMPT
==== 10) LOCOMOTIVES (join corrigido + NOT EXISTS, coluna fluel_capacity) ====

DECLARE
v_count NUMBER;
BEGIN
SELECT COUNT(*)
INTO v_count
FROM user_tables
WHERE table_name = 'FLUELTYPE';
IF
v_count = 0 THEN
    EXECUTE IMMEDIATE q'[
      CREATE TABLE FluelType
      (
          flueltype_id number(10) GENERATED AS IDENTITY,
          type        varchar2(8) NOT NULL,
          PRIMARY KEY (flueltype_id),
          CONSTRAINT CHK_flueltype_valid CHECK (type IN ('Diesel', 'Electric'))
      )
    ]';
END IF;
END;
/

-- Inserts from Excel: Locomotive -> tables FluelType, Gauge, Locomotive
MERGE INTO FluelType t USING (SELECT 'Diesel' type FROM dual UNION ALL SELECT 'Electric' FROM dual) s
ON (t.type=s.type)
WHEN NOT MATCHED THEN INSERT (type) VALUES (s.type);

MERGE INTO Gauge t USING (SELECT 1668 width_mm
                          FROM dual) s ON (t.width_mm = s.width_mm)
    WHEN NOT MATCHED THEN INSERT (width_mm, description) VALUES (s.width_mm, 'AUTO');

INSERT INTO Locomotive (flueltype_id, gauge_id, operator_id, name, model, fluel_capacity, power_kn, weight, make,
                        service_year, voltage_kv, frequency_hz)
SELECT (SELECT flueltype_id FROM FluelType WHERE type = 'Electric'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       'Inês',
       'Eurosprinter',
       NULL,
       0,
       0,
       'Siemens',
       2000,
       0,
       0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Locomotive WHERE name = 'Electric');

INSERT INTO Locomotive (flueltype_id, gauge_id, operator_id, name, model, fluel_capacity, power_kn, weight, make,
                        service_year, voltage_kv, frequency_hz)
SELECT (SELECT flueltype_id FROM FluelType WHERE type = 'Electric'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       'Paz',
       'Eurosprinter',
       NULL,
       0,
       0,
       'Siemens',
       2000,
       0,
       0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Locomotive WHERE name = 'Electric');

INSERT INTO Locomotive (flueltype_id, gauge_id, operator_id, name, model, fluel_capacity, power_kn, weight, make,
                        service_year, voltage_kv, frequency_hz)
SELECT (SELECT flueltype_id FROM FluelType WHERE type = 'Electric'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       'Helena',
       'Eurosprinter',
       NULL,
       0,
       0,
       'Siemens',
       2000,
       0,
       0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Locomotive WHERE name = 'Electric');

INSERT INTO Locomotive (flueltype_id, gauge_id, operator_id, name, model, fluel_capacity, power_kn, weight, make,
                        service_year, voltage_kv, frequency_hz)
SELECT (SELECT flueltype_id FROM FluelType WHERE type = 'Diesel'),
       (SELECT gauge_id FROM Gauge WHERE width_mm = 1668),
       (SELECT operator_id FROM Operator WHERE short_name = 'Medway'),
       'Eva',
       'CP 1900',
       4882,
       0,
       0,
       '	Sorefame - Alsthom',
       2000,
       0,
       0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Locomotive WHERE name = 'Diesel');


PROMPT
==== 11) TRAINS (mínimo para demonstrar relações) ====
INSERT INTO Train (name)
SELECT 'MINHO-FRT-01'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Train WHERE name = 'MINHO-FRT-01');
INSERT INTO Train (name)
SELECT 'MINHO-FRT-02'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Train WHERE name = 'MINHO-FRT-02');
INSERT INTO Train (name)
SELECT 'MINHO-FRT-03'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Train WHERE name = 'MINHO-FRT-03');

PROMPT
==== 12) TRAIN LINKS ====
INSERT INTO TrainLocomotive (train_id, loco_id)
SELECT (SELECT train_id FROM Train WHERE name = 'MINHO-FRT-01'),
       (SELECT MIN(loco_id) FROM Locomotive)
FROM dual
WHERE NOT EXISTS (SELECT 1
                  FROM TrainLocomotive
                  WHERE train_id = (SELECT train_id FROM Train WHERE name = 'MINHO-FRT-01'));

INSERT INTO TrainWagon (train_id, wagon_id)
SELECT (SELECT train_id FROM Train WHERE name = 'MINHO-FRT-01'),
       w.wagon_id
FROM (SELECT wagon_id FROM Wagon ORDER BY wagon_id FETCH FIRST 3 ROWS ONLY) w
WHERE NOT EXISTS (SELECT 1 FROM TrainWagon WHERE train_id = (SELECT train_id FROM Train WHERE name = 'MINHO-FRT-01'));

PROMPT
==== 13) TERMINALS & FREIGHTYARDS (mínimo) ====
INSERT INTO PostalCode (street, city, country)
SELECT 'AUTO-TERMINAL-1', 'Unknown', 'PT'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM PostalCode WHERE street = 'AUTO-TERMINAL-1');

INSERT INTO Address (postalCode_id, doorNumber)
SELECT (SELECT postalCode_id FROM PostalCode WHERE street = 'AUTO-TERMINAL-1'), 0
FROM dual
WHERE NOT EXISTS (SELECT 1
                  FROM Address
                  WHERE postalCode_id = (SELECT postalCode_id FROM PostalCode WHERE street = 'AUTO-TERMINAL-1')
                    AND doorNumber = 0);

INSERT INTO Facility (address_id, type, name)
SELECT (SELECT address_id
        FROM Address a
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-TERMINAL-1'
          AND a.doorNumber = 0),
       'Terminal',
       'Terminal AUTO 1'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Facility WHERE name = 'Terminal AUTO 1');

INSERT INTO Terminal (facility_id, capacity)
SELECT f.facility_id, 500
FROM Facility f
WHERE f.type = 'Terminal'
  AND NOT EXISTS (SELECT 1 FROM Terminal t WHERE t.facility_id = f.facility_id);

INSERT INTO PostalCode (street, city, country)
SELECT 'AUTO-FYARD-1', 'Unknown', 'PT'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM PostalCode WHERE street = 'AUTO-FYARD-1');

INSERT INTO Address (postalCode_id, doorNumber)
SELECT (SELECT postalCode_id FROM PostalCode WHERE street = 'AUTO-FYARD-1'), 0
FROM dual
WHERE NOT EXISTS (SELECT 1
                  FROM Address
                  WHERE postalCode_id = (SELECT postalCode_id FROM PostalCode WHERE street = 'AUTO-FYARD-1')
                    AND doorNumber = 0);

INSERT INTO Facility (address_id, type, name)
SELECT (SELECT address_id
        FROM Address a
                 JOIN PostalCode p ON a.postalCode_id = p.postalCode_id
        WHERE p.street = 'AUTO-FYARD-1'
          AND a.doorNumber = 0),
       'FreightYard',
       'FY AUTO 1'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Facility WHERE name = 'FY AUTO 1');

INSERT INTO FreightYard (facility_id)
SELECT f.facility_id
FROM Facility f
WHERE f.type = 'FreightYard'
  AND NOT EXISTS (SELECT 1 FROM FreightYard y WHERE y.facility_id = f.facility_id);

PROMPT
==== 14) FREIGHTS & CARGO (mínimo) ====
INSERT INTO Freight (origin_station_id, destination_station_id, train_id, departure_time, arrival_time)
SELECT (SELECT MIN(s1.station_id) FROM Station s1),
       (SELECT MAX(s2.station_id) FROM Station s2),
       (SELECT MIN(train_id) FROM Train),
       TO_DATE('2025-05-20 08:00', 'YYYY-MM-DD HH24:MI'),
       TO_DATE('2025-05-20 10:30', 'YYYY-MM-DD HH24:MI')
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Freight);

INSERT INTO Cargo (wagon_id, description, type, quantity, volume, weight, expiry_date)
SELECT w.wagon_id, 'Cereal load', 'Agricultural', 100, 50, 56000, TO_DATE('2025-12-31', 'YYYY-MM-DD')
FROM (SELECT wagon_id FROM Wagon ORDER BY wagon_id FETCH FIRST 3 ROWS ONLY) w
WHERE NOT EXISTS (SELECT 1 FROM Cargo);

PROMPT
==== DONE ====
