package net.perfectdreams.gabriela.models

import java.util.*

class Review(val id: UUID, val userId: String, val content: String, val upvotes: Int, val downvotes: Int)