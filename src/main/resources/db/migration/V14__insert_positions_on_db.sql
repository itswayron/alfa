INSERT INTO position (floor, side, "column", box)
SELECT floor, side, "column", box
FROM (
    SELECT f AS floor, s AS side, c AS "column", b AS box
    FROM unnest(array['1','2']) AS f,
         unnest(array['L','R']) AS s,
         unnest(array['1','2','3']) AS c,
         unnest(array['1','2','3','4','5']) AS b
) AS positions;
