package hu.bme.aut.thesis.freshfitness.model.state

import hu.bme.aut.thesis.freshfitness.model.social.Post

data class SocialState(
    // The detailed post displayed right side of the screen on tablets
    var detailedPost: Post? = null,
    // True if the feed is loading
    var isLoading: Boolean = true,
    // True if the user loads the next page of the posts
    var isLoadingMore: Boolean = true,
    var nextPage: Int = 0,
    var lastFetchedCount: Int = 10,
    // True if the user is logged in
    var isLoggedIn: Boolean = false,
    // Username of the user
    var username: String = "",
    // True if likes dialog should be shown
    var showLikesDialog: Boolean = false,
    // True if likes dialog should be shown
    var showCommentsDialog: Boolean = false,
    // True if comments dialog should be shown
    var showAddCommentDialog: Boolean = false,
    // True if CreatePost dialog should be shown
    var showCreatePostDialog: Boolean = false,
    // False if the buttons on CreatePostDialog are disabled (while uploading a post)
    var postCreationButtonsEnabled: Boolean = false,
    // True if likes dialog should be shown
    var showDeletePostAlert: Boolean = false,
    // True if likes dialog should be shown
    var showDeleteCommentAlert: Boolean = false,
    // True if likes dialog should be shown
    var showPostOptionsDialog: Boolean = false,
    // Id of the post that we are showing the options for
    var showPostOptionsFor: Int = -1,
    // True if comment options dialog should be shown
    var showCommentOptionsDialog: Boolean = false,
    // Id of the comment that we are showing the options for
    var showCommentOptionsFor: Int = -1,
    // True if likes dialog should be shown
    var showUploadState: Boolean = false,
    // 0 to 1 according to uploaded fractions
    var uploadState: Double = 0.0,
    var uploadText: String = "",
    // True if image is viewed on fullscreen
    var showImageFullScreen: Boolean = false,
    // location of the image we are showing full screen
    var fullScreenImageLocation: String = ""
)