
SET ECHO OFF
SET FEEDBACK ON
SET PAGESIZE 50000
SET LINESIZE 300
SET TRIMSPOOL ON
SET TAB OFF

SET COLSEP ';'
ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '.,';


SELECT
    g.gauge_id       AS "GAUGE_ID",
    g.width_mm       AS "WIDTH_MM",
    g.description    AS "DESCRIPTION"
FROM Gauge g
ORDER BY g.width_mm;


SELECT
    ft.facilitytype_id AS "FACILITYTYPE_ID",
    ft.type            AS "TYPE"
FROM FacilityType ft
ORDER BY ft.facilitytype_id;


SELECT
    f.facility_id   AS "FACILITY_ID",
    f.name          AS "NAME",
    f.location      AS "LOCATION",
    ft.type         AS "FACILITY_TYPE"
FROM Facility f
         JOIN FacilityType ft ON ft.facilitytype_id = f.facilitytype_id
ORDER BY f.name;


SELECT
    o.owner_id     AS "OWNER_ID",
    o.name         AS "NAME",
    o.short_name   AS "SHORT_NAME",
    o.vat_number   AS "VAT_NUMBER"
FROM Owner o
ORDER BY o.owner_id;


SELECT
    op.operator_id AS "OPERATOR_ID",
    op.name        AS "NAME",
    op.short_name  AS "SHORT_NAME",
    op.country     AS "COUNTRY",
    op.vat_number  AS "VAT_NUMBER"
FROM Operator op
ORDER BY op.operator_id;


SELECT
    wt.wagontype_id AS "WAGONTYPE_ID",
    wt.type         AS "TYPE"
FROM WagonType wt
ORDER BY wt.wagontype_id;

SELECT
    wm.wagonmodel_id       AS "WAGONMODEL_ID",
    wm.name                AS "NAME",
    wm.maker               AS "MAKER",
    wtype.type             AS "WAGON_TYPE",
    g.width_mm             AS "GAUGE_WIDTH_MM",
    wm.length_mm           AS "LENGTH_MM",
    wm.height_mm           AS "HEIGHT_MM",
    wm.width_mm            AS "WIDTH_MM",
    wm.weight_t            AS "WEIGHT_T",
    wm.payload_t           AS "PAYLOAD_T",
    wm.volume_m3           AS "VOLUME_M3",
    wm.number_of_bogies    AS "NUMBER_OF_BOGIES",
    wm.bogies              AS "BOGIES",
    wm.max_speed_kmh       AS "MAX_SPEED_KMH"
FROM WagonModel wm
         JOIN WagonType wtype ON wtype.wagontype_id = wm.wagontype_id
         JOIN Gauge g         ON g.gauge_id        = wm.gauge_id
ORDER BY wm.wagonmodel_id;

SELECT
    l.loco_id               AS "LOCO_ID",
    l.name                  AS "NAME",
    l.make                  AS "MAKE",
    l.model                 AS "MODEL",
    op.short_name           AS "OPERATOR",
    g.width_mm              AS "GAUGE_WIDTH_MM",
    l.length_m              AS "LENGTH_M",
    l.height_m              AS "HEIGHT_M",
    l.width_m               AS "WIDTH_M",
    l.power_kw              AS "POWER_KW",
    l.weight_t              AS "WEIGHT_T",
    l.service_year          AS "SERVICE_YEAR",
    l.max_speed_kmh         AS "MAX_SPEED_KMH",
    l.operational_speed_kmh AS "OPERATIONAL_SPEED_KMH",
    l.traction_kn           AS "TRACTION_KN",
    l.number_of_bogies      AS "NUMBER_OF_BOGIES",
    l.bogies                AS "BOGIES"
FROM Locomotive l
         JOIN Operator op ON op.operator_id = l.operator_id
         JOIN Gauge g     ON g.gauge_id     = l.gauge_id
ORDER BY l.loco_id;

SELECT
    el.loco_id     AS "LOCO_ID",
    el.voltage_kv  AS "VOLTAGE_KV",
    el.frequency_hz AS "FREQUENCY_HZ"
FROM Electric_Locomotive el
ORDER BY el.loco_id;


SELECT
    dl.loco_id       AS "LOCO_ID",
    dl.fuel_capacity AS "FUEL_CAPACITY"
FROM Diesel_Locomotive dl
ORDER BY dl.loco_id;

SELECT
    rl.line_id         AS "LINE_ID",
    rl.name            AS "NAME",
    g.width_mm         AS "GAUGE_WIDTH_MM",
    ow.short_name      AS "OWNER_SHORT",
    fs.name            AS "START_STATION",
    fe.name            AS "END_STATION"
FROM RailwayLine rl
         JOIN Gauge g    ON g.gauge_id    = rl.gauge_id
         JOIN Owner ow   ON ow.owner_id   = rl.owner_id
         JOIN Facility fs ON fs.facility_id = rl.start_station_id
         JOIN Facility fe ON fe.facility_id = rl.end_station_id
ORDER BY rl.line_id;

SELECT
    ls.line_segment_id AS "LINE_SEGMENT_ID",
    ls.line_id         AS "LINE_ID",
    rl.name            AS "LINE_NAME",
    ls.segment_order   AS "SEGMENT_ORDER",
    ls.length_m        AS "LENGTH_M",
    ls.speed_limit     AS "SPEED_LIMIT",
    ls.max_weight      AS "MAX_WEIGHT",
    ls.track_type      AS "TRACK_TYPE",
    ls.electrified     AS "ELECTRIFIED"
FROM LineSegment ls
         JOIN RailwayLine rl ON rl.line_id = ls.line_id
ORDER BY ls.line_id, ls.segment_order;

SELECT
    w.wagon_id         AS "WAGON_ID",
    op.short_name      AS "OPERATOR",
    wm.name            AS "WAGON_MODEL",
    w.service_year     AS "SERVICE_YEAR"
FROM Wagon w
         JOIN Operator  op ON op.operator_id   = w.operator_id
         JOIN WagonModel wm ON wm.wagonmodel_id = w.wagonmodel_id
ORDER BY w.wagon_id;

SELECT t.train_id AS "TRAIN_ID", t.name AS "NAME"
FROM Train t
ORDER BY t.train_id;

SELECT
    tw.train_id AS "TRAIN_ID",
    t.name      AS "TRAIN_NAME",
    tw.wagon_id AS "WAGON_ID"
FROM TrainWagon tw
         JOIN Train t ON t.train_id = tw.train_id
ORDER BY tw.train_id, tw.wagon_id;

SELECT
    tl.train_id AS "TRAIN_ID",
    t.name      AS "TRAIN_NAME",
    tl.loco_id  AS "LOCO_ID"
FROM TrainLocomotive tl
         JOIN Train t ON t.train_id = tl.train_id
ORDER BY tl.train_id, tl.loco_id;

SELECT
    c.cargo_id     AS "CARGO_ID",
    c.wagon_id     AS "WAGON_ID",
    c.description  AS "DESCRIPTION",
    c.type         AS "TYPE",
    c.quantity     AS "QUANTITY",
    c.volume       AS "VOLUME",
    c.weight       AS "WEIGHT",
    c.expiry_date  AS "EXPIRY_DATE"
FROM Cargo c
ORDER BY c.cargo_id;

SELECT
    f.freight_id          AS "FREIGHT_ID",
    f.train_id            AS "TRAIN_ID",
    f.origin_station_id   AS "ORIGIN_STATION_ID",
    f.destination_station_id AS "DESTINATION_STATION_ID",
    f.departure_time      AS "DEPARTURE_TIME",
    f.arrival_time        AS "ARRIVAL_TIME"
FROM Freight f
ORDER BY f.freight_id;
