package com.campus.gui;

import com.campus.CampusLife;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 容器类型注册
 */
public class CampusContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
        DeferredRegister.create(ForgeRegistries.CONTAINERS, CampusLife.MOD_ID);

    public static final RegistryObject<ContainerType<IncubatorContainer>> INCUBATOR_CONTAINER =
        CONTAINERS.register("incubator",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new IncubatorContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<AuctionContainer>> AUCTION_CONTAINER =
        CONTAINERS.register("auction",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new AuctionContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<CryptoContainer>> CRYPTO_CONTAINER =
        CONTAINERS.register("crypto",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new CryptoContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<ContractContainer>> CONTRACT_CONTAINER =
        CONTAINERS.register("contract",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new ContractContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<ESGContainer>> ESG_CONTAINER =
        CONTAINERS.register("esg",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new ESGContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<IPOContainer>> IPO_CONTAINER =
        CONTAINERS.register("ipo",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new IPOContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<VCContainer>> VC_CONTAINER =
        CONTAINERS.register("vc",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new VCContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<StockContainer>> STOCK_CONTAINER =
        CONTAINERS.register("stock",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new StockContainer(windowId, inv, buf.readBlockPos())));

    // === v17 新增 ===
    public static final RegistryObject<ContainerType<LoanContainer>> LOAN_CONTAINER =
        CONTAINERS.register("loan",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new LoanContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<PatentContainer>> PATENT_CONTAINER =
        CONTAINERS.register("patent",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new PatentContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<MergerContainer>> MERGER_CONTAINER =
        CONTAINERS.register("merger",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new MergerContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<MarketingContainer>> MARKETING_CONTAINER =
        CONTAINERS.register("marketing",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new MarketingContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<RealtyContainer>> REALTY_CONTAINER =
        CONTAINERS.register("realty",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new RealtyContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<EmployeeContainer>> EMPLOYEE_CONTAINER =
        CONTAINERS.register("employee",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new EmployeeContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<MetaverseContainer>> METAVERSE_CONTAINER =
        CONTAINERS.register("metaverse",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new MetaverseContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<InsuranceContainer>> INSURANCE_CONTAINER =
        CONTAINERS.register("insurance",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new InsuranceContainer(windowId, inv, buf.readBlockPos())));

    // === v18 新增 ===
    public static final RegistryObject<ContainerType<TalentContainer>> TALENT_CONTAINER =
        CONTAINERS.register("talent",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new TalentContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<NotaryContainer>> NOTARY_CONTAINER =
        CONTAINERS.register("notary",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new NotaryContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<CarbonContainer>> CARBON_CONTAINER =
        CONTAINERS.register("carbon",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new CarbonContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<SupplyContainer>> SUPPLY_CONTAINER =
        CONTAINERS.register("supply",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new SupplyContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<FranchiseContainer>> FRANCHISE_CONTAINER =
        CONTAINERS.register("franchise",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new FranchiseContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<TaxContainer>> TAX_CONTAINER =
        CONTAINERS.register("tax",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new TaxContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<TrainingContainer>> TRAINING_CONTAINER =
        CONTAINERS.register("training",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new TrainingContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<CourtContainer>> COURT_CONTAINER =
        CONTAINERS.register("court",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new CourtContainer(windowId, inv, buf.readBlockPos())));

    // === v19 新增 ===
    public static final RegistryObject<ContainerType<InvestorContainer>> INVESTOR_CONTAINER =
        CONTAINERS.register("investor",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new InvestorContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<GrantContainer>> GRANT_CONTAINER =
        CONTAINERS.register("grant",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new GrantContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<CRMContainer>> CRM_CONTAINER =
        CONTAINERS.register("crm",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new CRMContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<CompetitionContainer>> COMPETITION_CONTAINER =
        CONTAINERS.register("competition",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new CompetitionContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<ResearchContainer>> RESEARCH_CONTAINER =
        CONTAINERS.register("research",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new ResearchContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<LabContainer>> LAB_CONTAINER =
        CONTAINERS.register("lab",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new LabContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<MentorContainer>> MENTOR_CONTAINER =
        CONTAINERS.register("mentor",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new MentorContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<TrophyContainer>> TROPHY_CONTAINER =
        CONTAINERS.register("trophy",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new TrophyContainer(windowId, inv, buf.readBlockPos())));
}
