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

	public function execute(){
	    if ( $argc < 2 ) {
            exit("Usage: php ConstraintReport.php fileWithCommaSeperatedListOfItemsToCheck [numberOfItemsToCheck]");
	    }
	    if ( $argc == 3 ) {
	        $numberItemsToCheck = $argv[2];
	    } else {
	        $numberItemsToCheck = -1;
	    }
	    $itemsFile = file_get_contents( $argv[1] );
		$items = explode( ';', $itemsFile );
		$lookup = WikibaseRepo::getDefaultInstance()->getEntityLookup();

        $n = 0;
		foreach( $items as $item ){
			$itemId = 'Q' . $item;
			echo "$itemId\n";
			$entity = $lookup->getEntity( new ItemId( $itemId ) );
			if ( $entity ) {
				$service = new EvaluateConstraintReportJobService();
				$params = array( 'entityId' => $itemId, 'referenceTimestamp' => null );
				$resultSummary = $service->getResults( $params );
				$messageToLog = $service->buildMessageForLog( $resultSummary, null, $params );
				$service->writeToLog( $messageToLog );
				$n = $n + 1;
			}
			if ( $n >= $numberItemsToCheck ) {
				break;
			}
		}
	}
}

// @codeCoverageIgnoreStart
$maintClass = 'WikibaseQuality\ExternalValidation\Maintenance\CrossCheckFromFile';
require_once RUN_MAINTENANCE_IF_MAIN;