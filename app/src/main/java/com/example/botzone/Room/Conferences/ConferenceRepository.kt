package com.example.botzone.Room.Conferences



import com.example.botzone.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ConferenceRepository(
    private val dao: ConferenceDao
) {
    val allConferences: Flow<List<ConferenceEntity>> = dao.getAllConferences()
    suspend fun insertInitialData() {
    if (dao.getAllConferences().firstOrNull()?.isEmpty() != false) {

        val initialConferences = listOf(
            ConferenceEntity(
                title = "Global Robotics Symposium 2024",
                date = "Oct 24–26, 2024",
                location = "San Francisco, CA",
                speaker = "Dr. Eva Rostova",
                imageUrl = R.drawable.img7
            ),
            ConferenceEntity(
                title = "Innovate AI & Automation Summit",
                date = "Nov 12–14, 2024",
                location = "Berlin, Germany",
                speaker = "Prof. Kenji Tanaka",
                imageUrl = R.drawable.img8
            ),
            ConferenceEntity(
                title = "Medical Robotics Expo",
                date = "Dec 05–07, 2024",
                location = "Boston, MA",
                speaker = "Dr. Anya Sharma",
                imageUrl = R.drawable.img9
            )
            // می‌تونی بیشتر اضافه کنی
        )
        dao.insertAll(initialConferences)
    }

    }
}