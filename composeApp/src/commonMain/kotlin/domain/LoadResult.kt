package domain

sealed class LoadResult<out T> {

    data class Success<T>(
        val data: T
    ): LoadResult<T>()

    class Error: LoadResult<Nothing>()

}