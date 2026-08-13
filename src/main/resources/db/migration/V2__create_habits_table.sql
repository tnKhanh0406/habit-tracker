create table categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

create table habits (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50) NOT NULL DEFAULT 'brain',
    color VARCHAR(15) NOT NULL DEFAULT '#23A8F2',
    difficulty VARCHAR(20) NOT NULL DEFAULT 'EASY',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

create table habit_categories (
    id UUID PRIMARY KEY,
    habit_id UUID REFERENCES habits(id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(id) ON DELETE CASCADE
);

create table habit_schedules (
    id UUID PRIMARY KEY,
    habit_id UUID REFERENCES habits(id) ON DELETE CASCADE,
    schedule_type VARCHAR(20) NOT NULL DEFAULT 'DAILY',
    interval_value INT NOT NULL DEFAULT 1,
    start_date DATE NOT NULL DEFAULT CURRENT_DATE,
    end_date DATE
);

create table habit_schedule_weekdays (
    id UUID PRIMARY KEY,
    habit_schedule_id UUID REFERENCES habit_schedules(id) ON DELETE CASCADE,
    weekday INT NOT NULL CHECK (weekday >= 0 AND weekday <= 6)
);

CREATE TABLE habit_targets (
   id UUID PRIMARY KEY,
   habit_id UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
   value NUMERIC(10, 2) NOT NULL DEFAULT 1,
   unit VARCHAR(30) NOT NULL DEFAULT 'done',
   created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   CONSTRAINT chk_target_value CHECK (value > 0),
   CONSTRAINT uq_habit_target UNIQUE (habit_id)
);

CREATE TABLE habit_checkins (
    id UUID PRIMARY KEY,
    habit_id UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
    checkin_date DATE NOT NULL DEFAULT CURRENT_DATE,
    progress NUMERIC(10, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_habit_checkin UNIQUE (habit_id, checkin_date),
    CONSTRAINT chk_progress CHECK (progress >= 0)
);

create table habit_reminders (
    id UUID PRIMARY KEY,
    habit_id UUID REFERENCES habits(id) ON DELETE CASCADE,
    reminder_time TIME NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_habit_reminder_time UNIQUE (habit_id, reminder_time)
);