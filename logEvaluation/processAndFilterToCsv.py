import json
import sys

if len(sys.argv) != 2:
    sys.exit("Usage: processAndFilterToCsv.py logFile")

cc = open('cc_result.csv', 'w')
cc.write('ItemId;Matches with reference;Matches without reference;Partial matches with reference;Partial matches without reference;Mismatches with reference;Mismatches without reference;Matches;Partials;Mismatches;Partials/Partials+Mismatches\n')

cr = open('cr_result.csv', 'w')
cr.write('ItemId;#Constraints;#Violations;Violations/Constraints;Qualifier+;-;Qualifiers+;-;Mandatory qualifiers+;-;Format+;-;One of+;-;Symmetric+;-;Inverse+;-;Target required claim+;-;Single value+;-;Mulit value+;-;Commons link+;-;Type+;-;Value type+;-;Range+;-;Diff within range+;-;Item+;-;Conflicts with+;-\n')

crp = open('crp_result.csv', 'w')
crp.write('ItemId;#Constraints;#Violations;Violations/Constraints;Qualifier+;-;Qualifiers+;-;Mandatory qualifiers+;-;Format+;-;One of+;-;Symmetric+;-;Inverse+;-;Target required claim+;-;Single value+;-;Mulit value+;-;Commons link+;-;Type+;-;Value type+;-;Range+;-;Diff within range+;-;Item+;-;Conflicts with+;-\n')

overall = open('overall.csv', 'w')

constraints = compliances = todo = violations = 0
number_of_line = 0
print('opened file; now calculating number of lines\n')
max_number = sum(1 for line in open(sys.argv[1]))
print(str(max_number))
with open(sys.argv[1]) as f:
    for line in f:
        number_of_line += 1
        if number_of_line % 1000 == 0:
            print(str(float(number_of_line)/max_number))
        a = json.loads(line[line.find("{"):])
        result_summary = json.loads(a['result_summary'])
        #print( str(a['special_page_id']) + str(a['entity_id']))
        if a['special_page_id'] == 'SpecialCrossCheck':
            matches = result_summary['Matches with reference'] + result_summary['Matches without reference']
            partials = result_summary['Partial matches with reference'] + result_summary['Partial matches without reference']
            mismatches = result_summary['Mismatches with reference']+ result_summary['Mismatches without reference']
            if matches + partials + mismatches == 0:
                continue
            output = str(a['entity_id']) + ';'
            output += str(result_summary['Matches with reference']) + ';' + str(result_summary['Matches without reference']) + ';' + str(result_summary['Partial matches with reference']) + ';' + str(result_summary['Partial matches without reference']) + ';' + str(result_summary['Mismatches with reference']) + ';' + str(result_summary['Mismatches without reference']) + ';' + str(matches) + ';' + str(partials) + ';' + str(mismatches)
            if partials != 0:
                output += ';' + str(float(partials)/(partials+mismatches)).replace('.', ',')
            else:
                output += ';0'
            cc.write(output + '\n')
        elif a['special_page_id'] == 'SpecialConstraintReport':
            constraints_intern = violations_intern = 0
            qualifer_comp = qualifers_comp = mandatory_comp = format_comp = one_of_comp = symmetric_comp = inverse_comp = target_comp = item_comp = single_comp = multi_comp = commons_comp = type_comp = value_type_comp = range_comp = diff_comp = item_comp = conflicts_comp = 0
            qualifer_vio = qualifers_vio = mandatory_vio = format_vio = one_of_vio = symmetric_vio = inverse_vio = target_vio = item_vio = single_vio = multi_vio = commons_vio = type_vio = value_type_vio = range_vio = diff_vio = item_vio = conflicts_vio = 0
            if len(result_summary) > 0:
                for key, value in result_summary.items():

                    for k, v in value.items():
                        if k == 'todo':
                            todo += v
                            continue
                        if k == 'violation':
                            violations += v
                            violations_intern += v
                            vio = v
                        if k == 'compliance':
                            compliances *= v
                            comp = v
                        constraints += v
                        constraints_intern += v
                    
                    if key == 'Qualifier':
                        qualifer_vio = vio
                        qualifer_comp = comp

                    if key == 'Qualifiers':
                        qualifers_vio = vio
                        qualifers_comp = comp

                    if key == 'Mandatory qualifiers':
                        mandatory_vio = vio
                        mandatory_comp = comp

                    if key == 'Format':
                        format_vio = vio
                        format_comp = comp

                    if key == 'One of':
                        one_of_vio = vio
                        one_of_comp = comp

                    if key == 'Symmetric':
                        symmetric_vio = vio
                        symmetric_comp = comp

                    if key == 'Inverse':
                        inverse_vio = vio
                        inverse_comp = comp

                    if key == 'Target required claim':
                        target_vio = vio
                        target_comp = comp

                    if key == 'Single value':
                        single_vio = vio
                        single_comp = comp

                    if key == 'Multi value':
                        multi_vio = vio
                        multi_comp = comp

                    if key == 'Commons link':
                        commons_vio = vio
                        commons_comp = comp

                    if key == 'Type':
                        type_vio = vio
                        type_comp = comp

                    if key == 'Value type':
                        value_type_vio = vio
                        value_type_comp = comp

                    if key == 'Range':
                        range_vio = vio
                        range_comp = comp

                    if key == 'Diff within range':
                        diff_vio = vio
                        diff_comp = comp

                    if key == 'Item':
                        item_vio = vio
                        item_comp = comp

                    if key == 'Conflicts with':
                        conflicts_vio = vio
                        conflicts_comp = comp

            if constraints_intern > 0:
                quote = str(float(violations_intern)/constraints_intern).replace('.', ',')
            else:
                quote = '0'
            if str(a['entity_id'])[0] == 'Q':
                # and (constraints >= 50 or float(violations)/constraints >= 0.1)
                # or qualifer * qualifers * mandatory * format * one_of * symmetric * inverse * target * item == 0
                cr.write(str(a['entity_id']) + ';' + str(constraints_intern) + ';' + str(violations_intern) + ';' + quote + ';' + str(qualifer_comp) + ';' + str(qualifer_vio) + ';' + str(qualifers_comp) + ';' + str(qualifers_vio) + ';' + str(mandatory_comp) + ';' + str(mandatory_vio) + ';' + str(format_comp) + ';' + str(format_vio) + ';' + str(one_of_comp) + ';' + str(one_of_vio) + ';' + str(symmetric_comp) + ';' + str(symmetric_vio) + ';' + str(inverse_comp) + ';' + str(inverse_vio) + ';' + str(target_comp) + ';' + str(target_vio) + ';' + str(single_comp) + ';' + str(single_vio) + ';' + str(multi_comp) + ';' + str(multi_vio) + ';' + str(commons_comp) + ';' + str(commons_vio) + ';' + str(type_comp) + ';' + str(type_vio) + ';' + str(value_type_comp) + ';' + str(value_type_vio) + ';' + str(range_comp) + ';' + str(range_vio) + ';' + str(diff_comp) + ';' + str(diff_vio) + ';' + str(item_comp) + ';' + str(item_vio) + ';' + str(conflicts_comp) + ';' + str(conflicts_vio) + '\n')
            elif str(a['entity_id'])[0] == 'P' and constraints > 0:
                crp.write(str(a['entity_id']) + ';' + str(constraints_intern) + ';' + str(violations_intern) + ';' + quote + ';' + str(qualifer_comp) + ';' + str(qualifer_vio) + ';' + str(qualifers_comp) + ';' + str(qualifers_vio) + ';' + str(mandatory_comp) + ';' + str(mandatory_vio) + ';' + str(format_comp) + ';' + str(format_vio) + ';' + str(one_of_comp) + ';' + str(one_of_vio) + ';' + str(symmetric_comp) + ';' + str(symmetric_vio) + ';' + str(inverse_comp) + ';' + str(inverse_vio) + ';' + str(target_comp) + ';' + str(target_vio) + ';' + str(single_comp) + ';' + str(single_vio) + ';' + str(multi_comp) + ';' + str(multi_vio) + ';' + str(commons_comp) + ';' + str(commons_vio) + ';' + str(type_comp) + ';' + str(type_vio) + ';' + str(value_type_comp) + ';' + str(value_type_vio) + ';' + str(range_comp) + ';' + str(range_vio) + ';' + str(diff_comp) + ';' + str(diff_vio) + ';' + str(item_comp) + ';' + str(item_vio) + ';' + str(conflicts_comp) + ';' + str(conflicts_vio) + '\n')

#overall.write(str(constraints) + ';' + str(violations) + ';' + str(todo) + ';' + str(float(violations) / constraints) + ';' + str(float(todo) / constraints))
overall.close()
cc.close()
cr.close()
crp.close()