package courier.uy.core.services.interfaces;

public interface IMongoDBMigrationService {
    public void makeMongoDBMigration();
    public void makeUserMigrationToMongoDB();
    public void makeRoleMigrationToMongoDB();
    public void makeEmpresaMigrationToMongoDB();

    void makeEmpaqueMigrationToMongoDB();

    public void makeGrupoEmpresasMigrationToMongoDB();

    void makeProductoVisibilidadMigrationToMongoDB();

    public void makePresentacionMigrationToMongoDB();
    public void makeProductoMigrationToMongoDB();
    public void makeListaVentaMigrationToMongoDB();
    public void makeUbicacionMigrationToMongoDB();
    public void makeListaVentaProductoMigrationToMongoDB();
    public void makeUsuarioEmpresaMigrationToMongoDB();
    public void makeUsuarioEmpresaCodigoMigrationToMongoDB();
    public void makeUsuarioReseteoContrasenaMigrationToMongoDB();

    void makeWishlistMigrationToMongoDB();

    void makeParamMigrationToMongoDB();
}
