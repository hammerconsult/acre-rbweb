UPDATE parametromarcafogo
SET exercicio_id = (SELECT e.id FROM exercicio e WHERE e.ano = 2024)
WHERE id = (
    SELECT id
    FROM (
             SELECT id
             FROM parametromarcafogo
             WHERE exercicio_id IS NULL
             ORDER BY id
         )
    WHERE ROWNUM = 1
);
