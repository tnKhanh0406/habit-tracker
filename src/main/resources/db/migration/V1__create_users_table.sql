CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

   username VARCHAR(50) NOT NULL UNIQUE,

   email VARCHAR(255) NOT NULL UNIQUE,

   password_hash VARCHAR(255) NOT NULL,

   avatar_url TEXT,

   timezone VARCHAR(50),

   created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

   updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);