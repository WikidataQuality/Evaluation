<?php

namespace WikibaseQuality\ExternalValidation\Maintenance;

// @codeCoverageIgnoreStart
use Wikibase\DataModel\Entity\ItemId;
use Wikibase\Repo\WikibaseRepo;
use WikibaseQuality\ExternalValidation\EvaluateCrossCheckJobService;


$basePath = getenv( "MW_INSTALL_PATH" ) !== false ? getenv( "MW_INSTALL_PATH" ) : __DIR__ . "/../../..";
require_once $basePath . "/maintenance/Maintenance.php";
// @codeCoverageIgnoreEnd

class CrossCheckFromFile extends \Maintenance {

	public function __construct() {
		parent::__construct();
		$this->mDescription = "Checks constraints on items from file. Add --file and optional --number if you only want to the first x of them.";
		$this->addOption( 'file', 'file with semicolon-seperated numeric item ids you want to be checked', true, true );
		$this->addOption( 'number', 'number of items you want to be checked', false, true );
	}
		
	public function execute(){
	    if ( !$this->getOption( 'file' ) ) {
            exit("Usage: php ConstraintReport.php --fileWithSemicolonSeperatedListOfItemsToCheck [numberOfItemsToCheck]");
	    }
	    $numberItemsToCheck = $this->getOption( 'number' ) ? $this->getOptions( 'number' ) : -1;
		
	    $itemsFile = file_get_contents( $this->getOptions( 'file' ) );
		$items = explode( ';', $itemsFile );
		$lookup = WikibaseRepo::getDefaultInstance()->getEntityLookup();

        $n = 0;
		foreach( $items as $item ){
			$itemId = 'Q' . $item;
			echo "$itemId\n";
			$entity = $lookup->getEntity( new ItemId( $itemId ) );
			if ( $entity ) {
				$service = new EvaluateCrossCheckJobService();
				$params = array( 'entityId' => $itemId, 'referenceTimestamp' => null );
				$resultSummary = $service->getResults( $params );
				$messageToLog = $service->buildMessageForLog( $resultSummary, null, $params );
				$service->writeToLog( $messageToLog );
				$n += 1;
			}
			if ( $n === $numberItemsToCheck ) {
				break;
			}
		}
	}
}

// @codeCoverageIgnoreStart
$maintClass = 'WikibaseQuality\ExternalValidation\Maintenance\CrossCheckFromFile';
require_once RUN_MAINTENANCE_IF_MAIN;
