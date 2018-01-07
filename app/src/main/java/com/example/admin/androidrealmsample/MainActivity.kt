package com.example.admin.androidrealmsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.realm.*
import io.realm.annotations.PrimaryKey
import java.nio.file.Files.size
import io.realm.Realm.Transaction.OnSuccess
import io.realm.RealmObject
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        useRealm()
    }

    fun useRealm() {
        // 通常のJavaオブジェクトのようにモデルクラスを使用
        val dog = Dog()
        dog.name = "Rex"
        dog.age = 1

        // Realm全体の初期化
        Realm.init(this)

        // このスレッドのためのRealmインスタンスを取得
        val realm = Realm.getDefaultInstance()

        // 年齢が2未満のすべてのDogオブジェクトを取得する問い合わせを発行
        val puppies = realm.where(Dog::class.java).lessThan("age", 2).findAll()
        puppies.size // => まだRealmへは一つもオブジェクトが保存されていないので0を返します

        // トランザクションの中でデータを永続化します
        realm.beginTransaction()
        val managedDog = realm.copyToRealm(dog) // unmanagedなオブジェクトを永続化
        val person = realm.createObject(Person::class.java, UUID.randomUUID().toString()) // managedなオブジェクトを直接作成
        person.dogs!!.add(managedDog)

        realm.commitTransaction()

        // データが更新されると、リスナーが呼びだされて更新を受け取ることができます。
        puppies.addChangeListener(OrderedRealmCollectionChangeListener<RealmResults<Dog>> { results, changeSet ->
            // Query results are updated in real time with fine grained notifications.
            changeSet.insertions // => [0] is added.
        })

        // 別のスレッドから非同期に更新を実行することもできます
//        realm.executeTransactionAsync(object : Realm.Transaction() {
//            fun execute(bgRealm: Realm) {
//                // トランザクションの開始とコミットは自動的に行われます
//                val dog = bgRealm.where(Dog::class.java).equalTo("age", 1).findFirst()
//                dog.setAge(3)
//            }
//        }, object : Realm.Transaction.OnSuccess() {
//            fun onSuccess() {
//                // 既存のクエリやRealmObjectは自動的に最新の情報に更新されます
//                puppies.size // => 0("age"が2未満の犬は存在しなくなったので)
//                managedDog.getAge()   // => 3(既存オブジェクトは最新の状態に更新されるため)
//            }
//        })
    }
}


// RealmObjectを継承することでモデルクラスを定義

public open class Dog(
        public open var name: String = "",
        public open var age: Int = 0
) : RealmObject()


public open class Person(
        @PrimaryKey public open var id: String = "",
        public open var name: String = "",
        public open var dogs: RealmList<Dog>? = null // 1対多の関連をもつ
) : RealmObject()
