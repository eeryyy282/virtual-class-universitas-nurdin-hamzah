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
    @Query("SELECT * FROM forums WHERE kelas_id = :kelasId")
    fun getForumsByClass(kelasId: String): Flow<List<ForumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForum(forum: ForumEntity): Long

    @Query("SELECT * FROM posts WHERE forum_id = :forumId ORDER BY tanggal_post ASC")
    fun getPostsByForum(forumId: Int): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Query("SELECT forum_id FROM forums WHERE judul_forum = :judul AND kelas_id = :kelasId ORDER BY forum_id DESC LIMIT 1")
    suspend fun getForumIdByTitleAndClassId(
        judul: String,
        kelasId: String,
    ): Int?
}
