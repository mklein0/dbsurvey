#
import sqlalchemy
import phoenixdb
import phoenixdb.cursor

HOST = 'phoenix'
# PORT = 32778
PORT = 8765
database_url = 'http://{}:{}/'.format(HOST, PORT)

# # DB API Test
# conn = phoenixdb.connect(database_url, autocommit=True)
#
# cursor = conn.cursor()
# cursor.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username VARCHAR)")
# cursor.execute("UPSERT INTO users VALUES (?, ?)", (1, 'admin'))
# cursor.execute("SELECT * FROM users")
# print(cursor.fetchall())
#
# cursor = conn.cursor(cursor_factory=phoenixdb.cursor.DictCursor)
# cursor.execute("SELECT * FROM users WHERE id=1")
# print(cursor.fetchone()['USERNAME'])
#
# # SQL Alchemy test
# schema_name = None
# table_name = 'USERS'  # Phoenix normalizes to UPPERCASE
#
# engine = sqlalchemy.create_engine('phoenix://{host}:{port}/'.format(host=HOST, port=PORT))
#
# tabledef = sqlalchemy.Table(table_name, sqlalchemy.MetaData(), autoload=True, autoload_with=engine, schema=schema_name)
# print(tabledef)
# print(tuple(tabledef.columns))
#
#
# # table_name = 'WIDE_TABLE_100K'  # Phoenix normalizes to UPPERCASE
# schema_name = 'EXAMPLE'
# table_name = 'ANALYTICS_LACQUER'  # Phoenix normalizes to UPPERCASE
#
# tabledef = sqlalchemy.Table(table_name, sqlalchemy.MetaData(), autoload=True, autoload_with=engine, schema=schema_name)
# print(tabledef)
# print(tuple(tabledef.columns))

try:
    from ma.statengine.base import StatEngine
    from ma.statengine.phoenix.statengine import PhoenixStatEngine
    from ma.models.StatTable import StatTable
    from ma.Session import Session
except ImportError:
    import sys
    sys.exit(0)

Session().new('demo')

import ipdb; ipdb.set_trace()
# Make sure can connect to driver class
st_engine: PhoenixStatEngine = StatEngine.new_engine('phx')

# Setup example stat table
st = StatTable(schema_name='example', name='analytics_lacquer', statengine='phx')

tabledef = st_engine.get_table(st)
# print(tuple(tabledef.columns))

# import ipdb; ipdb.set_trace()
# print(st.query(select=[{'name': '_id'}], limit=5))
print(st.query(select=[{'name': '_id'}], limit=1))
