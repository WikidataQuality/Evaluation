<?php

namespace WikibaseQuality\ExternalValidation\Maintenance;

// @codeCoverageIgnoreStart
use Wikibase\DataModel\Entity\ItemId;
use Wikibase\Repo\WikibaseRepo;
use WikibaseQuality\ExternalValidation\EvaluateCrossCheckJobService;


$basePath = getenv( "MW_INSTALL_PATH" ) !== false ? getenv( "MW_INSTALL_PATH" ) : __DIR__ . "/../../..";
require_once $basePath . "/maintenance/Maintenance.php";
// @codeCoverageIgnoreEnd

class CrossCheck extends \Maintenance {

	public function execute(){
	    if ( $argc == 2 ) {
	        $numberItemsToCheck = $argv[1];
	    } else {
	        // when no upper bound is given, try to check all of them
	        $numberItemsToCheck = 20000000;
	    }

        $n = 0;
        $i = 1;
		while( $n <= $numberItemsToCheck {
			$itemId = 'Q' . $item;
			echo "$itemId\n";
			$i += 1;
			$entity = $lookup->getEntity( new ItemId( $itemId ) );
			if ( $entity ) {
				$service = new EvaluateConstraintReportJobService();
				$params = array( 'entityId' => $itemId, 'referenceTimestamp' => null );
				$resultSummary = $service->getResults( $params );
				$messageToLog = $service->buildMessageForLog( $resultSummary, null, $params );
				$service->writeToLog( $messageToLog );
				$n = $n + 1;
			}
		}
	}
}

// @codeCoverageIgnoreStart
$maintClass = 'WikibaseQuality\ExternalValidation\Maintenance\CrossCheck';
require_once RUN_MAINTENANCE_IF_MAIN;