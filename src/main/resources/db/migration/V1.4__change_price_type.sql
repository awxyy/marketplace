ALTER TABLE products
    ALTER COLUMN price TYPE double precision
        USING price::double precision;
