-- ============================================================
-- ROUTE TABLE - Para armazenar rotas da USLP08
-- ============================================================

CREATE TABLE Route (
                       route_id NUMBER(10),
                       route_type VARCHAR2(10) CONSTRAINT NN_Route_Type NOT NULL
        CONSTRAINT CHK_Route_Type CHECK (route_type IN ('SIMPLE', 'COMPLEX')),
                       created_date DATE DEFAULT SYSDATE,
                       CONSTRAINT PK_Route PRIMARY KEY (route_id)
);

-- Associar Freights à Route
ALTER TABLE Freight ADD route_id NUMBER(10);
ALTER TABLE Freight ADD CONSTRAINT FK_Freight_Route FOREIGN KEY (route_id) REFERENCES Route(route_id);

-- RouteStop - Sequence de facilities na rota
CREATE TABLE RouteStop (
                           route_stop_id NUMBER(10),
                           route_id NUMBER(10) CONSTRAINT NN_RouteStop_Route NOT NULL,
                           facility_id NUMBER(10) CONSTRAINT NN_RouteStop_Facility NOT NULL,
                           stop_order NUMBER(10) CONSTRAINT NN_RouteStop_Order NOT NULL
        CONSTRAINT CHK_RouteStop_Order CHECK (stop_order > 0),
                           CONSTRAINT PK_RouteStop PRIMARY KEY (route_stop_id),
                           CONSTRAINT UQ_RouteStop_Route_Order UNIQUE (route_id, stop_order)
);

ALTER TABLE RouteStop ADD CONSTRAINT FK_RouteStop_Route FOREIGN KEY (route_id) REFERENCES Route(route_id);
ALTER TABLE RouteStop ADD CONSTRAINT FK_RouteStop_Facility FOREIGN KEY (facility_id) REFERENCES Facility(facility_id);

-- Atualizar Train para ter route_id
ALTER TABLE Train ADD route_id NUMBER(10);
ALTER TABLE Train ADD CONSTRAINT FK_Train_Route FOREIGN KEY (route_id) REFERENCES Route(route_id);