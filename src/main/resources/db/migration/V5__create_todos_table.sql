CREATE TABLE todos (
   id UUID PRIMARY KEY,
   title VARCHAR(200) NOT NULL,
   description TEXT,
   completed BOOLEAN NOT NULL DEFAULT false,
   difficulty VARCHAR(20) NOT NULL DEFAULT 'EASY',
   due_date DATE,
   due_time TIME,
   completed_at TIMESTAMPTZ,
   user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
   created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE todo_subtasks (
   id UUID PRIMARY KEY,
   todo_id UUID NOT NULL REFERENCES todos(id) ON DELETE CASCADE,
   title VARCHAR(200) NOT NULL,
   completed BOOLEAN NOT NULL DEFAULT FALSE,
   position INT NOT NULL DEFAULT 0,
   created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE todo_categories (
    id UUID PRIMARY KEY,
    todo_id UUID NOT NULL REFERENCES todos(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE(todo_id, category_id)
);

CREATE TABLE todo_reminders (
    id UUID PRIMARY KEY,
    todo_id UUID NOT NULL REFERENCES todos(id) ON DELETE CASCADE,
    reminder_time TIMESTAMPTZ NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);