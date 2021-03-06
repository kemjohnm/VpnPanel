package models.dao

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import slick.profile.RelationalProfile
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import models.domain.User
import security.Permissions

@Singleton
final private [models] class UserDAO @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[RelationalProfile] {
  import slick.driver.MySQLDriver.api._
  import Permissions._

  protected class UsersTable(tag: Tag) extends Table[User](tag, "USERS") {
    def id = column[UUID]("ID", O.PrimaryKey, O.AutoInc)
    def username = column[String]("USERNAME", O.Length(255, true))
    def password = column[String]("PASSWORD", O.Length(255, true))
    def `type` = column[Permissions]("TYPE")
    def credits = column[Int]("CREDITS")
    def createdBy = column[Option[String]]("CREATED_BY")

    def * = (id.?, username, password, `type`, credits, createdBy) <> (User.tupled, User.unapply)
  }

  private [models] object query extends TableQuery(new UsersTable(_)) {
    @inline def apply(id: UUID) = this.withFilter(_.id === id)
    @inline def apply(username: String, password: String) =
      this.withFilter(r => r.username === r.username && r.password === password)
  }
}
