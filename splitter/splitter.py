def split_insert_or_update_script(sql_script, chunk_size=1000, conflict_target="id"):
    insert_start = sql_script.split("VALUES")[0] + "VALUES "
    values_section = sql_script.split("VALUES")[1].strip().rstrip(";")
    values = values_section.split("),")
    values = [v.strip() + ")" if not v.endswith(")") else v.strip() for v in values]

    chunks = []
    for i in range(0, len(values), chunk_size):
        chunk_values = ', '.join(values[i:i + chunk_size])
        conflict_clause = f" ON CONFLICT ({conflict_target}) DO UPDATE SET task_id = excluded.task_id, user_id = excluded.user_id, label_id = excluded.label_id, batch_id = excluded.batch_id, polygon = excluded.polygon;"
        chunk = f"{insert_start}{chunk_values}{conflict_clause}"
        chunks.append(chunk)

    return chunks

if __name__ == '__main__':
    folder = "../../Dijon-to-correct-batch3-1777"
    with open(f"{folder}/V6_annotation.sql", "r") as file:
        sql_script = file.read().replace(', %s', '')
        sql_script = sql_script[0:len(sql_script)-2]+';'
        chunks = split_insert_or_update_script(sql_script, chunk_size=1000, conflict_target="id")

    for index, chunk in enumerate(chunks):
        file_name = f'{folder}/split_insert_part_{index + 1}.sql'
        with open(file_name, 'w') as f:
            f.write(chunk)
            print(f"Written {file_name}")