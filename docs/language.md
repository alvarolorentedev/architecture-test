# Language

Among the multiple languages purposed it was decided to use kotlin.

The comparison in between languages is the next one: 

| Language  | Pros | Cons |
| ------------- | ------------- | ------------- |
| Java  | - A general knowledge<br> - Knowledge in the company<br> - Native kafka streams libraries<br> | - Not a functional language |
| Kotlin  | - OO and functional<br> - extending to different platforms<br> - Good adoption rate<br> - currently attracting talent<br> - Big players behind (Google with android)<br>  | - Kafka libraries not native |
| Scala  |  - OO and functional<br> - Native kafka streams libraries<br> | - Learning curve<br> - Difficult to hire|
| Javascript  | - Very extended language<br> - Can target all platform<br> | - Doubts on performance in data streaming environments<br> - Community Kafka streams library not fully functional |

With the whys expresed on the previous table and having seen ktor is already being used in the producer (some knowledge on the company) it seemed the best decision to go with Kotlin.