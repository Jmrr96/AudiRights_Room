package com.example.koalaappm13.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionRepository(private val sessionDao: SessionDao) {

    suspend fun insertSession(session: Session) {
        withContext(Dispatchers.IO) {
            sessionDao.insertSession(session)
        }
    }

    suspend fun getAllSessions(): List<Session> {
        return withContext(Dispatchers.IO) {
            sessionDao.getAllSessions()
        }
    }
}
