package com.mjs.core.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mjs.core.data.source.local.entity.ForumEntity
import com.mjs.core.data.source.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForumDao {
    @Query("SELECT * FROM forums WHERE course_id = :courseId")
    fun getForumsByCourse(courseId: String): Flow<List<ForumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForum(forum: ForumEntity): Long

    @Query("SELECT * FROM posts WHERE forum_id = :forumId ORDER BY post_date ASC")
    fun getPostsByForumId(forumId: Long): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)
}
