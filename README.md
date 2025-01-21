# Ktor Demo Application

このプロジェクトは、Ktorクライアントを使用してJSONPlaceholderからデータを取得し、Jetpack Composeで表示するデモアプリケーションです。

## 技術スタック

- Kotlin
- Jetpack Compose
- Ktor Client
- Kotlinx Serialization

## セットアップ

### ライブラリの導入

build.gradle.kts (app)に以下を追加：

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.21"  // Kotlinxシリアライゼーション用
}

dependencies {
    // Ktor関連
    implementation(libs.ktor.client.android.v237)           // Ktorのアンドロイドクライアント
    implementation(libs.ktor.client.content.negotiation)    // コンテンツネゴシエーション
    implementation(libs.ktor.serialization.kotlinx.json)    // JSONシリアライゼーション
    implementation(libs.kotlinx.serialization.json)         // Kotlinxシリアライゼーション
}
```

## コード構成

### データモデル (Post.kt)
```kotlin
@Serializable
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)
```

### ViewModel (MainViewModel.kt)
```kotlin
class MainViewModel : ViewModel() {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true  // 未知のJSONフィールドを無視
                isLenient = true          // JSONパースを寛容に
                prettyPrint = true        // 整形されたJSON出力
                useArrayPolymorphism = true  // 配列のポリモーフィズムを使用
            })
        }
    }
    
    var posts by mutableStateOf<List<Post>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                isLoading = true
                posts = client.get("https://jsonplaceholder.typicode.com/posts").body()
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
```

### UI実装 (MainActivity.kt)
```kotlin
@Composable
fun PostScreen(
    viewModel: MainViewModel = MainViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchPosts()
    }

    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                viewModel.isLoading -> CircularProgressIndicator()
                viewModel.error != null -> Text(viewModel.error ?: "")
                else -> PostList(posts = viewModel.posts)
            }
        }
    }
}
```

## 主な機能

1. **APIクライアント**
   - Ktorを使用したHTTPクライアントの実装
   - JSONシリアライゼーション
   - エラーハンドリング

2. **状態管理**
   - ViewModelによるUI状態の管理
   - ローディング状態の制御
   - エラー状態の管理

3. **UI**
   - Jetpack ComposeによるモダンなUI実装
   - Material Design 3コンポーネントの使用
   - レスポンシブなレイアウト

## 特徴

- **型安全性**: Kotlinx Serializationによる型安全なJSON処理
- **非同期処理**: Coroutinesを使用した効率的な非同期処理
- **クリーンアーキテクチャ**: ViewModelパターンによる関心の分離
- **モダンUI**: Jetpack Composeによる宣言的UI

## 必要システム要件

- Android Studio Hedgehog | 2023.1.1 以上
- compileSdk 35
- minSdk 24
- targetSdk 34